package xuml.tools.model.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.xml.bind.JAXBElement;

import xuml.tools.miuml.metamodel.jaxb.Class;
import xuml.tools.miuml.metamodel.jaxb.Domains;
import xuml.tools.miuml.metamodel.jaxb.ModeledDomain;
import xuml.tools.miuml.metamodel.jaxb.Subsystem;
import xuml.tools.miuml.metamodel.jaxb.SubsystemElement;
import xuml.tools.model.compiler.runtime.CreationEvent;
import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.Event;
import xuml.tools.model.compiler.runtime.SignalProcessorListenerFactory;
import xuml.tools.model.compiler.runtime.Signaller;

import com.google.common.collect.Lists;

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

	public CodeGeneratorJava(Domains domains, String domainName,
			String domainPackageName, String domainSchema,
			File entitySourceDirectory, File resourcesDirectory,
			String implementationPackageName,
			File implementationSourceDirectory, boolean generatePersistenceXml,
			boolean overwriteImplementation) {
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

	public void generate() {
		generateEntitySources();
	}

	private void generateEntitySources() {
		log("generating " + entitySourceDirectory);
		ModeledDomain md = domain;
		Lookups lookups = new Lookups(domains, md);
		for (Class cls : getClasses(md)) {
			createEntityJavaSource(cls, entitySourceDirectory, lookups);
			createImplementationJavaSource(cls, implementationSourceDirectory,
					lookups);
		}
		if (generatePersistenceXml)
			createPersistenceXml(domain, new File(resourcesDirectory,
					"META-INF/persistence.xml"));
		createContext(domain, entitySourceDirectory, lookups);
		log("finished generation");
	}

	private void createImplementationJavaSource(Class cls, File destination,
			Lookups lookups) {
		ClassInfo info = createClassInfo(cls);
		if (info.hasBehaviour()) {
			log("generating " + getFullClassImplementationName(cls));
			BehaviourImplementationWriter w = new BehaviourImplementationWriter(
					info, getFullClassImplementationName(cls));
			String java = w.generate();
			File file = new File(destination,
					getClassImplementationFilename(cls));
			if (!file.exists() || overwriteImplementation)
				writeToFile(java.getBytes(), file);
		}
	}

	private String getClassImplementationFilename(Class cls) {
		String s = getFullClassImplementationName(cls);
		return s.replace(".", "/") + ".java";
	}

	private String getFullClassImplementationName(Class cls) {
		return implementationPackageName + "." + getClassJavaSimpleName(cls)
				+ "Behaviour";
	}

	private void createPersistenceXml(ModeledDomain domain, File file) {
		OutputStream out;
		try {
			file.getParentFile().mkdirs();
			out = new FileOutputStream(file);
			String xml = generatePersistenceXml(domain);
			out.write(xml.toString().getBytes());
			out.close();
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
		String xml = new PersistenceXmlWriter().generate(classes);
		return xml;
	}

	private List<Class> getClasses(ModeledDomain domain) {
		List<Class> list = Lists.newArrayList();
		for (Subsystem subsystem : domain.getSubsystem()) {
			for (JAXBElement<? extends SubsystemElement> element : subsystem
					.getSubsystemElement()) {
				if (element.getValue() instanceof Class) {
					Class cls = (Class) element.getValue();
					list.add(cls);
				}
			}
		}
		return list;
	}

	private void createContext(ModeledDomain domain, File destination,
			Lookups lookups) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);

		TypeRegister types = new TypeRegister();
		out.format("public class Context {\n\n");
		out.format("    private static %s signaller;\n\n",
				types.addType(Signaller.class));
		out.format("    public static int sendSignalsInQueue() {\n");
		out.format("        return signaller.sendSignalsInQueue();\n");
		out.format("    }\n\n");
		out.format(
				"    public static <T extends %s<T>> long persistSignal(Object id, Class<T> cls, %s<T> event) {\n",
				types.addType(Entity.class), types.addType(Event.class));
		out.format("        return signaller.persistSignal(id,cls,event);\n");
		out.format("    }\n\n");
		out.format("    public static void stop() {\n");
		out.format("        signaller.stop();\n");
		out.format("    }\n\n");
		out.format(
				"    public static <T extends %s<T>> T create(%s<T> cls, %s<T> event) {\n",
				types.addType(Entity.class),
				types.addType(java.lang.Class.class),
				types.addType(CreationEvent.class));
		out.format("        return signaller.create(cls,event);\n");
		out.format("    }\n\n");
		out.format(
				"    public static void setEntityManagerFactory(%s emf) {\n",
				types.addType(EntityManagerFactory.class));
		out.format("        signaller = new %s(emf,listenerFactory);\n",
				types.addType(Signaller.class), types.addType(Signaller.class));
		for (Subsystem subsystem : domain.getSubsystem()) {
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
		out.format(
				"    public static void setEntityActorListenerFactory(%s listenerFactory) {\n",
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
		out.format("    public static void close() {\n");
		out.format("        signaller.close();\n");
		out.format("    }\n\n");

		out.format(
				"    public static <T extends %s<T>> T remove(T entity) {\n",
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

		out.format("    public static %s em() {\n",
				types.addType(EntityManager.class));
		out.format("        return signaller.getInfo().getCurrentEntityManager();\n");
		out.format("    }\n\n");

		out.format("}");
		out.close();

		String s = "package " + domainPackageName + ";\n\n";
		s += types.getImports(domainPackageName + ".Context") + "\n";
		s += bytes.toString();

		String filename = domainPackageName.replace(".", "/") + "/Context.java";
		try {
			FileOutputStream fos = new FileOutputStream(new File(destination,
					filename));
			fos.write(s.getBytes());
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void log(String message) {
		java.lang.System.out.println(message);
	}

	private void createEntityJavaSource(Class cls, File destination,
			Lookups lookups) {
		ClassWriter w = new ClassWriter(createClassInfo(cls));
		String java = w.generate();
		File file = new File(destination, getClassFilename(cls));
		writeToFile(java.getBytes(), file);
	}

	private ClassInfo createClassInfo(Class cls) {
		Lookups lookups = new Lookups(domains, domain);
		return new ClassInfo(nameManager, cls, domainPackageName,
				"description", domainSchema, lookups);
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
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}