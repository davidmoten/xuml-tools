package xuml.tools.jaxb.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.persistence.EntityManagerFactory;
import javax.xml.bind.JAXBElement;

import miuml.jaxb.Class;
import miuml.jaxb.ModeledDomain;
import miuml.jaxb.Subsystem;
import miuml.jaxb.SubsystemElement;

public class CodeGeneratorJava {

	private final String contextPackageName;
	private final File resourcesDirectory;
	private final ModeledDomain domain;
	private final String domainPackageName;

	public CodeGeneratorJava(miuml.jaxb.ModeledDomain domain,
			String domainPackageName, String contextPackageName,
			File resourcesDirectory) {
		this.domain = domain;
		this.domainPackageName = domainPackageName;
		this.contextPackageName = contextPackageName;
		this.resourcesDirectory = resourcesDirectory;
	}

	public void generate(File destination) {

		ModeledDomain md = domain;
		Lookups lookups = new Lookups(md);
		for (Subsystem subsystem : md.getSubsystem()) {
			for (JAXBElement<? extends SubsystemElement> element : subsystem
					.getSubsystemElement()) {
				if (element.getValue() instanceof Class) {
					Class cls = (Class) element.getValue();
					// create classes (impls)
					createImplementation(cls, destination, lookups);
					// create behaviour interfaces
					createBehaviourInterface(cls, destination);
					createBehaviourFactoryInterface(cls, destination);
				}
			}
			createContext(destination);

			// create object factory
			// createObjectFactory(domain, destination);

			// createPersistenceXml(domain.getClazz(), resourcesDirectory);
		}
		log("finished generation");
	}

	private void log(String message) {
		java.lang.System.out.println(message);
	}

	// private void createPersistenceXml(List<Class> clazz, File
	// resourcesDirectory) {
	// List<String> list = Lists.newArrayList();
	// for (Class cls : clazz) {
	// ClassInfo info = createClassInfo(cls);
	// list.add(info.getClassFullName());
	// }
	// String xml = new PersistenceXmlWriter().generate(list);
	// try {
	// File file = new File(resourcesDirectory, "META-INF/persistence.xml");
	// file.getParentFile().mkdirs();
	// FileUtils.write(file, xml);
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// }

	private void createContext(File destination) {
		TypeRegister types = new TypeRegister();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		types.addType(EntityManagerFactory.class);
		out.format("package %s;\n\n", contextPackageName);
		out.format("IMPORTS_HERE\n");
		out.format("public class Context {\n\n");
		out.format("    private static EntityManagerFactory emf;\n\n");
		out.format("    public static EntityManagerFactory getEntityManagerFactory() {\n");
		out.format("        return emf;\n");
		out.format("    }\n\n");
		out.format("    public static void setEntityManagerFactory(EntityManagerFactory value){\n");
		out.format("        emf = value;\n");
		out.format("    }\n");
		for (Subsystem ss : domain.getSubsystem()) {
			for (JAXBElement<? extends SubsystemElement> element : ss
					.getSubsystemElement()) {
				if (element.getValue() instanceof Class) {
					Class cls = (Class) element.getValue();
					ClassInfo info = createClassInfo(cls);
					String behaviourFactory = types.addType(info
							.getBehaviourFactoryFullName());
					out.format("    private static %s %s;\n\n",
							behaviourFactory,
							info.getBehaviourFactoryFieldName());
					out.format("    public static void set%s(%s factory){\n",
							info.getBehaviourFactorySimpleName(),
							behaviourFactory);
					out.format("        %s=factory;\n",
							info.getBehaviourFactoryFieldName());
					out.format("    }\n\n");
					out.format("    public static %s get%s(){\n",
							behaviourFactory,
							info.getBehaviourFactorySimpleName());
					out.format("        return %s;\n",
							info.getBehaviourFactoryFieldName());
					out.format("    }\n\n");
				}
			}
		}

		out.format("}\n");
		out.close();

		File file = new File(destination, contextPackageName.replace(".", "/")
				+ "/Context.java");
		String java = bytes.toString().replace("IMPORTS_HERE",
				types.getImports());
		writeToFile(java.getBytes(), file);

	}

	private void createImplementation(Class cls, File destination,
			Lookups lookups) {
		ClassWriter w = new ClassWriter(createClassInfo(cls));
		String java = w.generate();
		File file = new File(destination, getClassFilename(cls));
		writeToFile(java.getBytes(), file);
	}

	private ClassInfo createClassInfo(Class cls) {
		Lookups lookups = new Lookups(domain);
		return new ClassInfoFromJaxb2(cls, domainPackageName, "description",
				"schema", "table", lookups);
	}

	//
	// private void createObjectFactory(System system2, File destination) {
	//
	// }
	//
	private void createBehaviourInterface(Class cls, File destination) {

		destination.mkdirs();
		// add operations, performOnEntry methods
		File file = new File(destination, getClassBehaviourFilename(cls));

		TypeRegister types = new TypeRegister();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		String pkg = getPackage(cls);
		out.format("public interface %sBehaviour {\n\n", cls.getName());
		// TODO
		// for (Event event : cls.getEvent()) {
		// String typeName = types.addType(new Type(pkg + "." + cls.getName()
		// + ".Events." + upperFirst(event.getName())));
		// out.format("    void onEntry(%s event);\n\n", typeName);
		// }

		out.format("}");
		out.close();

		String java = "package " + pkg + ".behaviour;\n\n";
		java += types.getImports();
		java += "\n";
		String all = java + bytes.toString();
		writeToFile(all.getBytes(), file);
	}

	private void createBehaviourFactoryInterface(Class cls, File destination) {
		TypeRegister types = new TypeRegister();
		ClassInfo info = createClassInfo(cls);
		String java = "package " + getPackage(cls) + ".behaviour;\n\n";
		java += "IMPORTS_HERE\n";
		java += "public interface " + cls.getName() + "BehaviourFactory {\n\n";
		types.addType(getFullClassName(cls) + "Behaviour");
		types.addType(getFullClassName(cls));
		java += "    " + getClassJavaSimpleName(cls) + "Behaviour create("
				+ info.addType(info.getClassFullName()) + " cls);\n";
		java += "}";
		java = java.replace("IMPORTS_HERE", info.getImports());
		File file = new File(destination, getClassBehaviourFactoryFilename(cls));
		writeToFile(java.getBytes(), file);
	}

	//
	// // ----------------------------------------
	// // Utility Methods
	// // -----------------------------------------
	private String getPackage(Class cls) {
		return domainPackageName;
	}

	//

	private String getClassJavaSimpleName(Class cls) {
		return cls.getName().replace(" ", "").replace("-", "");
	}

	//
	private String getFullClassName(Class cls) {
		return domainPackageName + "." + getClassJavaSimpleName(cls);
	}

	private String getClassBehaviourFilename(Class cls) {
		String s = getFullClassName(cls);
		int i = s.lastIndexOf(".");
		if (i == -1)
			s = "behaviour." + s;
		else
			s = s.substring(0, i) + ".behaviour" + s.substring(i);
		return s.replace(".", "/") + "Behaviour.java";
	}

	private String getClassFilename(Class cls) {
		String s = getFullClassName(cls);
		return s.replace(".", "/") + ".java";
	}

	private String getClassBehaviourFactoryFilename(Class cls) {
		String s = getFullClassName(cls);
		int i = s.lastIndexOf(".");
		if (i == -1)
			s = "behaviour." + s;
		else
			s = s.substring(0, i) + ".behaviour" + s.substring(i);
		return s.replace(".", "/") + "BehaviourFactory.java";
	}

	// ----------------------------------------
	// Static Utility Methods
	// -----------------------------------------

	private static void writeToFile(byte[] bytes, File file) {
		try {
			file.getParentFile().mkdirs();
			java.lang.System.out.println("writing to " + file);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	//
	// private static void re(String string) {
	// throw new RuntimeException(string);
	// }

}