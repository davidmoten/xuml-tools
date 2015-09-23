package xuml.tools.model.compiler;

import static xuml.tools.model.compiler.Util.getClasses;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.xml.bind.JAXBElement;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import xuml.tools.miuml.metamodel.jaxb.Class;
import xuml.tools.miuml.metamodel.jaxb.Domains;
import xuml.tools.miuml.metamodel.jaxb.LocalEffectiveSignalingEvent;
import xuml.tools.miuml.metamodel.jaxb.ModeledDomain;
import xuml.tools.miuml.metamodel.jaxb.Subsystem;
import xuml.tools.miuml.metamodel.jaxb.SubsystemElement;
import xuml.tools.model.compiler.runtime.CreationEvent;
import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.Event;
import xuml.tools.model.compiler.runtime.QueuedSignal;
import xuml.tools.model.compiler.runtime.SignalProcessorListenerFactory;
import xuml.tools.model.compiler.runtime.Signaller;

/**
 * Generates code associated with one modeled domain.
 * 
 * @author dxm
 * 
 */
public class CodeGeneratorJava {

    private final ModeledDomain domain;
    private final String domainPackageName;
    private final String domainSchema;
    private final Domains domains;
    private final File resourcesDirectory;
    private final boolean generatePersistenceXml;
    private final NameManager nameManager;
    private final File entitySourceDirectory;
    private final String implementationPackageName;
    private final File implementationSourceDirectory;
    private final boolean overwriteImplementation;

    public CodeGeneratorJava(Domains domains, String domainName, String domainPackageName,
            String domainSchema, File entitySourceDirectory, File resourcesDirectory,
            String implementationPackageName, File implementationSourceDirectory,
            boolean generatePersistenceXml, boolean overwriteImplementation) {
        Preconditions.checkNotNull(domains);
        Preconditions.checkNotNull(domainName);
        Preconditions.checkNotNull(domainPackageName);
        Preconditions.checkNotNull(domainSchema);
        Preconditions.checkNotNull(entitySourceDirectory);
        Preconditions.checkNotNull(resourcesDirectory);
        Preconditions.checkNotNull(implementationPackageName);
        Preconditions.checkNotNull(implementationSourceDirectory);

        this.domains = domains;
        this.entitySourceDirectory = entitySourceDirectory;
        this.resourcesDirectory = resourcesDirectory;
        this.implementationPackageName = implementationPackageName;
        this.implementationSourceDirectory = implementationSourceDirectory;
        this.generatePersistenceXml = generatePersistenceXml;
        this.overwriteImplementation = overwriteImplementation;
        this.domain = Util.getModeledDomain(domains, domainName);
        this.domainPackageName = domainPackageName;
        this.domainSchema = domainSchema;
        this.nameManager = new NameManager();
    }

    public static Builder builder() {
        return new Builder();
    }

    public void generate() {
        generateEntitySources();
    }

    private void generateEntitySources() {
        log("generating " + entitySourceDirectory);
        ModeledDomain md = domain;
        Lookups lookups = new Lookups(domains, md);
        for (Class cls : getClasses(md)) {
            createEntityJavaSource(cls, entitySourceDirectory, lookups);
            // createImplementationJavaSource(cls,
            // implementationSourceDirectory,
            // lookups);
        }
        createStateMachineTables(getClasses(md),
                new File(resourcesDirectory, "state-transitions.html"));
        if (generatePersistenceXml)
            createPersistenceXml(domain, new File(resourcesDirectory, "META-INF/persistence.xml"));
        createContext(domain, entitySourceDirectory, lookups);
        log("finished generation");
    }

    private static void createStateMachineTables(List<Class> classes, File file) {
        file.getParentFile().mkdirs();
        try (PrintStream out = new PrintStream(file)) {
            out.println("<html>");
            out.println("<head>");
            out.println("<style>");
            out.println("table, th, td {\n" + "    border: 1px solid black;\n"
                    + "    border-collapse: collapse;\n" + "}\n" + "th, td {\n"
                    + "    padding: 15px;\n" + "}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            for (Class cls : classes) {
                createStateMachineTable(cls, out);
            }
            out.println("</body>");
            out.println("</html>");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createStateMachineTable(Class cls, PrintStream out) {
        if (cls.getLifecycle() == null)
            return;
        List<String> states = cls.getLifecycle().getState().stream().map(state -> state.getName())
                .sorted().collect(Collectors.toList());
        out.println();
        out.format("<h2>%s</h2>\n", cls.getName());
        out.format("<table>\n");
        out.format("<tr><th></th>%s</tr>",
                states.stream().map(s -> "<th>" + s + "</th>").collect(Collectors.joining()));
        for (String state1 : states) {
            out.format("<tr><th>%s</th>", state1);
            for (String state2 : states) {
                String eventNames = cls.getLifecycle().getTransition().stream()
                        .filter(t -> t.getState().equals(state1)
                                && t.getDestination().equals(state2))
                        .map(t -> t.getEventID()).map(eventId -> eventName(cls, eventId))
                        .collect(Collectors.joining("<br/>"));
                out.format("<td>%s</td>", eventNames);
            }
            out.println("</tr>");
        }
        out.println("</table>");
    }

    private static String eventName(Class cls, BigInteger eventId) {
        return cls.getLifecycle().getEvent().stream().flatMap(event -> {
            if (event.getValue() instanceof xuml.tools.miuml.metamodel.jaxb.CreationEvent) {
                xuml.tools.miuml.metamodel.jaxb.CreationEvent creation = (xuml.tools.miuml.metamodel.jaxb.CreationEvent) event
                        .getValue();
                if (creation.getID().equals(eventId))
                    return Stream.of(creation.getName());
                else
                    return Stream.empty();
            } else if (event.getValue() instanceof LocalEffectiveSignalingEvent) {
                LocalEffectiveSignalingEvent local = (LocalEffectiveSignalingEvent) event
                        .getValue();
                if (local.getID().equals(eventId))
                    return Stream.of(local.getName());
                else
                    return Stream.empty();
            } else
                return Stream.empty();
        }).findAny().orElseThrow(RuntimeException::new);
    }

    private void createImplementationJavaSource(Class cls, File destination, Lookups lookups) {
        ClassInfo info = createClassInfo(cls);
        if (info.hasBehaviour()) {
            log("generating " + getFullClassImplementationName(cls));
            BehaviourImplementationWriter w = new BehaviourImplementationWriter(info,
                    getFullClassImplementationName(cls));
            String java = w.generate();
            File file = new File(destination, getClassImplementationFilename(cls));
            if (!file.exists() || overwriteImplementation)
                writeToFile(java.getBytes(), file);
        }
    }

    private String getClassImplementationFilename(Class cls) {
        String s = getFullClassImplementationName(cls);
        return s.replace(".", "/") + ".java";
    }

    private String getFullClassImplementationName(Class cls) {
        return implementationPackageName + "." + getClassJavaSimpleName(cls) + "Behaviour";
    }

    private void createPersistenceXml(ModeledDomain domain, File file) {
        try {
            file.getParentFile().mkdirs();
            try (FileOutputStream out = new FileOutputStream(file)) {
                String xml = generatePersistenceXml(domain);
                out.write(xml.toString().getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generatePersistenceXml(ModeledDomain domain) {
        List<String> classes = Lists.newArrayList();
        for (Class cls : getClasses(domain)) {
            ClassInfo info = createClassInfo(cls);
            classes.add(info.getClassFullName());
        }
        classes.add(QueuedSignal.class.getName());
        String xml = new PersistenceXmlWriter().generate(classes);
        return xml;
    }

    private void createContext(ModeledDomain domain, File destination, Lookups lookups) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);

        TypeRegister types = new TypeRegister();
        out.format("public class Context {\n\n");
        out.format("    private static volatile %s signaller;\n\n", types.addType(Signaller.class));
        out.format("    public static int sendSignalsInQueue() {\n");
        out.format("        return signaller.sendSignalsInQueue();\n");
        out.format("    }\n\n");
        out.format("    public static long queueSize() {\n");
        out.format("        return signaller.queueSize();\n");
        out.format("    }\n\n");
        out.format("    public static %s<%s> queuedSignals() {\n", types.addType(List.class),
                types.addType(QueuedSignal.class));
        out.format("        return signaller.queuedSignals();\n");
        out.format("    }\n\n");
        out.format(
                "    public static <T extends %s<T>> long persistSignal(String fromEntityUniqueId, Object id, Class<T> cls, %s<T> event, long time, %s<Long> repeatIntervalMs, String entityUniqueId) {\n",
                types.addType(Entity.class), types.addType(Event.class),
                types.addType(Optional.class));
        out.format(
                "        return signaller.persistSignal(fromEntityUniqueId, id, cls, event, time, repeatIntervalMs, entityUniqueId);\n");
        out.format("    }\n\n");
        out.format("    public synchronized static void stop() {\n");
        out.format("        if (signaller != null) {\n");
        out.format("            signaller.stop();\n");
        out.format("        }\n");
        out.format("    }\n\n");
        out.format("    public static <T extends %s<T>> T create(%s<T> cls, %s<T> event) {\n",
                types.addType(Entity.class), types.addType(java.lang.Class.class),
                types.addType(CreationEvent.class));
        out.format("        return signaller.create(cls,event);\n");
        out.format("    }\n\n");
        out.format("    public synchronized static void setEntityManagerFactory(%s emf) {\n",
                types.addType(EntityManagerFactory.class));
        out.format("        signaller = new %s(emf,listenerFactory);\n",
                types.addType(Signaller.class), types.addType(Signaller.class));
        for (

        Subsystem subsystem : domain.getSubsystem())

        {
            for (JAXBElement<? extends SubsystemElement> element : subsystem
                    .getSubsystemElement()) {
                if (element.getValue() instanceof Class) {
                    Class cls = (Class) element.getValue();
                    // create classes (impls)
                    ClassInfo info = createClassInfo(cls);
                    if (info.hasBehaviour())
                        out.format("        %s.setSignaller_(signaller);\n",
                                types.addType(info.getClassFullName()));
                }
            }
        }
        out.format("    }\n\n");

        out.format("    private static %s listenerFactory;\n\n",
                types.addType(SignalProcessorListenerFactory.class));
        out.format("    public static void setEntityActorListenerFactory(%s listenerFactory) {\n",
                types.addType(SignalProcessorListenerFactory.class));
        out.format("        if (signaller !=null)\n");
        out.format(
                "            throw new %s(\"EntityActorListenerFactory must be set before EntityManagerFactory\");\n",
                types.addType(RuntimeException.class));
        out.format("        Context.listenerFactory = listenerFactory;\n");
        out.format("    }\n\n");

        out.format("    public static %s createEntityManager() {\n",
                types.addType(EntityManager.class));
        out.format("        return signaller.getEntityManagerFactory().createEntityManager();\n");
        out.format("    }\n\n");
        out.format("    public synchronized static void close() {\n");
        out.format("        if (signaller != null) {\n");
        out.format("            signaller.close();\n");
        out.format("            signaller = null;\n");
        out.format("        }\n");
        out.format("    }\n\n");

        out.format("    public static <T extends %s<T>> T remove(T entity) {\n",
                types.addType(Entity.class));
        out.format("        boolean emOpenAlready = em()!=null;\n");
        out.format("        %s em;\n", types.addType(EntityManager.class));
        out.format("        if (emOpenAlready)\n");
        out.format("            em = em();\n");
        out.format("        else\n");
        out.format("            em = createEntityManager();\n");
        out.format("        em.remove(entity);\n");
        out.format("        if (!emOpenAlready)\n");
        out.format("            em.close();\n");
        out.format("        return entity;\n");
        out.format("    }\n\n");

        out.format("    public static <T extends %s<T>> T load(T entity) {\n",
                types.addType(Entity.class));
        out.format("        boolean emOpenAlready = em()!=null;\n");
        out.format("        %s em;\n", types.addType(EntityManager.class));
        out.format("        if (emOpenAlready)\n");
        out.format("            em = em();\n");
        out.format("        else\n");
        out.format("            em = createEntityManager();\n");
        out.format("        T t = em.merge(entity);\n");
        out.format("        em.refresh(t);\n");
        out.format("        if (!emOpenAlready)\n");
        out.format("            em.close();\n");
        out.format("        return t;\n");
        out.format("    }\n\n");

        out.format("    public static %s em() {\n", types.addType(EntityManager.class));
        out.format("        return signaller.getInfo().getCurrentEntityManager();\n");
        out.format("    }\n\n");

        out.format("}");
        out.close();

        String s = "package " + domainPackageName + ";\n\n";
        s += types.getImports(domainPackageName + ".Context") + "\n";
        s += bytes.toString();

        String filename = domainPackageName.replace(".", "/") + "/Context.java";
        try

        {
            try (FileOutputStream fos = new FileOutputStream(new File(destination, filename))) {
                fos.write(s.getBytes());
            }
        } catch (

        IOException e)

        {
            throw new RuntimeException(e);
        }

    }

    private static void log(String message) {
        java.lang.System.out.println(message);
    }

    private void createEntityJavaSource(Class cls, File destination, Lookups lookups) {
        ClassWriter w = new ClassWriter(createClassInfo(cls));
        String java = w.generate();
        File file = new File(destination, getClassFilename(cls));
        writeToFile(java.getBytes(), file);
    }

    private ClassInfo createClassInfo(Class cls) {
        Lookups lookups = new Lookups(domains, domain);
        return new ClassInfo(nameManager, cls, domainPackageName, domainSchema, lookups);
    }

    private String getClassJavaSimpleName(Class cls) {
        return cls.getName().replace(" ", "").replace("-", "");
    }

    private String getFullClassName(Class cls) {
        return domainPackageName + "." + getClassJavaSimpleName(cls);
    }

    private String getClassFilename(Class cls) {
        String s = getFullClassName(cls);
        return s.replace(".", "/") + ".java";
    }

    // ----------------------------------------
    // Static Utility Methods
    // -----------------------------------------

    private static void writeToFile(byte[] bytes, File file) {
        try {
            file.getParentFile().mkdirs();
            log("writing to " + file);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder {
        private String domainName;
        private String domainPackageName;
        private String domainSchema;
        private Domains domains;
        private File resourcesDirectory;
        private boolean generatePersistenceXml;
        private File entitySourceDirectory;
        private final String implementationPackageName = "not used yet";
        private File implementationSourceDirectory;
        private final boolean overwriteImplementation = false;

        private Builder() {

        }

        public Builder domains(Domains domains) {
            this.domains = domains;
            return this;
        }

        public Builder domainName(String domainName) {
            this.domainName = domainName;
            return this;
        }

        public Builder domainSchema(String domainSchema) {
            this.domainSchema = domainSchema;
            return this;
        }

        public Builder domainPackageName(String packageName) {
            this.domainPackageName = packageName;
            return this;
        }

        public Builder generatedResourcesDirectory(File directory) {
            this.resourcesDirectory = directory;
            return this;
        }

        public Builder generatedResourcesDirectory(String directory) {
            this.resourcesDirectory = new File(directory);
            return this;
        }

        public Builder generatePersistenceXml(boolean generate) {
            this.generatePersistenceXml = generate;
            return this;
        }

        public Builder generatedSourcesDirectory(File directory) {
            this.entitySourceDirectory = directory;
            return this;
        }

        public Builder generatedSourcesDirectory(String directory) {
            this.entitySourceDirectory = new File(directory);
            return this;
        }

        public CodeGeneratorJava build() {
            if (implementationSourceDirectory == null)
                implementationSourceDirectory = entitySourceDirectory;
            return new CodeGeneratorJava(domains, domainName, domainPackageName, domainSchema,
                    entitySourceDirectory, resourcesDirectory, implementationPackageName,
                    implementationSourceDirectory, generatePersistenceXml, overwriteImplementation);
        }

    }

}