package xuml.tools.model.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBElement;

import miuml.jaxb.Class;
import miuml.jaxb.Domains;
import miuml.jaxb.ModeledDomain;
import miuml.jaxb.Subsystem;
import miuml.jaxb.SubsystemElement;

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