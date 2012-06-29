package xuml.tools.jaxb.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.bind.JAXBElement;

import miuml.jaxb.Class;
import miuml.jaxb.Domains;
import miuml.jaxb.ModeledDomain;
import miuml.jaxb.Subsystem;
import miuml.jaxb.SubsystemElement;
import xuml.tools.jaxb.compiler.ClassInfo.MyEvent;
import xuml.tools.jaxb.compiler.ClassInfo.MyTransition;

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

	public CodeGeneratorJava(Domains domains, String domainName,
			String domainPackageName, String domainSchema) {
		this.domains = domains;
		this.domain = Util.getModeledDomain(domains, domainName);
		this.domainPackageName = domainPackageName;
		this.domainSchema = domainSchema;
	}

	public void generate(File destination) {

		ModeledDomain md = domain;
		Lookups lookups = new Lookups(domains, md);
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

			// create object factory
			// createObjectFactory(domain, destination);

			// createPersistenceXml(domain.getClazz(), resourcesDirectory);
		}
		log("finished generation");
	}

	private void log(String message) {
		java.lang.System.out.println(message);
	}

	private void createImplementation(Class cls, File destination,
			Lookups lookups) {
		ClassWriter w = new ClassWriter(createClassInfo(cls));
		String java = w.generate();
		File file = new File(destination, getClassFilename(cls));
		writeToFile(java.getBytes(), file);
	}

	private ClassInfo createClassInfo(Class cls) {
		Lookups lookups = new Lookups(domains, domain);
		return new ClassInfoFromJaxb(cls, domainPackageName, "description",
				domainSchema, lookups);
	}

	//
	// private void createObjectFactory(System system2, File destination) {
	//
	// }
	//
	private void createBehaviourInterface(Class cls, File destination) {

		ClassInfo info = createClassInfo(cls);
		if (info.getEvents().size() == 0)
			return;

		destination.mkdirs();
		// add operations, performOnEntry methods
		File file = new File(destination, getClassBehaviourFilename(cls));

		TypeRegister types = new TypeRegister();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		String pkg = getPackage(cls);
		out.format("public interface %sBehaviour {\n\n", cls.getName());

		for (MyEvent event : info.getEvents()) {
			for (MyTransition transition : info.getTransitions()) {
				// constraint is no event overloading
				if (transition.getEventName().equals(event.getName())) {
					out.format("    void onEntry%s(%s event);\n\n", Util
							.upperFirst(Util.toJavaIdentifier(transition
									.getToState())), types.addType(info
							.getClassFullName()
							+ ".Events."
							+ event.getSimpleClassName()));
				}
			}
		}

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
		if (info.getEvents().size() == 0)
			return;

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