package xuml.tools.model.compiler;

import java.io.File;
import java.io.FileNotFoundException;

import miuml.jaxb.Marshaller;

import org.junit.Test;

public class CodeGeneratorJavaTest {

	@Test
	public void testCodeGenerationForABC() throws FileNotFoundException {
		generateClassesForDomain("Nested composite id example", "abc", "abc");
	}

	@Test
	public void testCodeGenerationAllTypes() throws FileNotFoundException {
		generateClassesForDomain("all-types");
	}

	@Test
	public void testCodeGenerationForBookstore() throws FileNotFoundException {
		generateClassesForDomain("Bookstore");
	}

	@Test
	public void testCodeGenerationForOneToOne() throws FileNotFoundException {
		generateClassesForDomain("one-to-one");
	}

	@Test
	public void testCodeGenerationForOneToOneMany()
			throws FileNotFoundException {
		generateClassesForDomain("one-to-one-many");
	}

	@Test
	public void testCodeGenerationForOneToZeroOne()
			throws FileNotFoundException {
		generateClassesForDomain("one-to-zero-one");
	}

	@Test
	public void testCodeGenerationForSecondaryIdentifiers()
			throws FileNotFoundException {
		generateClassesForDomain("secondary-identifiers");
	}

	@Test
	public void testCodeGenerationForSpecialization()
			throws FileNotFoundException {
		generateClassesForDomain("specialization");
	}

	@Test
	public void testCodeGenerationForUnaryToOne() throws FileNotFoundException {
		generateClassesForDomain("unary-one");
	}

	@Test
	public void testCodeGenerationForUnaryToZeroOne()
			throws FileNotFoundException {
		generateClassesForDomain("unary-zero-one");
	}

	@Test
	public void testCodeGenerationForZeroOneToMany()
			throws FileNotFoundException {
		generateClassesForDomain("zero-one-to-many");
	}

	@Test
	public void testCodeGenerationForZeroOneToOneMany()
			throws FileNotFoundException {
		generateClassesForDomain("zero-one-to-one-many");
	}

	@Test
	public void testCodeGenerationForExtensions() throws FileNotFoundException {
		generateClassesForDomain("extensions");
	}

	@Test
	public void testCodeGenerationForManyToMany() throws FileNotFoundException {
		generateClassesForDomain("many-to-many");
	}

	@Test
	public void testCodeGenerationForManyToManyWithAssociationClass()
			throws FileNotFoundException {
		generateClassesForDomain("many-to-many-association-class");
	}

	private void generateClassesForDomain(String domainName) {
		String underscored = domainName.replaceAll("-", "_").toLowerCase();
		generateClassesForDomain(domainName, underscored, underscored);
	}

	private void generateClassesForDomain(String domainName,
			String domainPackageName, String schema) {
		xuml.tools.miuml.metamodel.jaxb.Domains domains = new Marshaller().unmarshal(getClass()
				.getResourceAsStream("/samples.xml"));
		File resources = new File("target/generated-resources");
		if (!resources.exists())
			resources.mkdirs();
		new CodeGeneratorJava(domains, domainName, domainPackageName, schema,
				"target", true).generate(new File("target/generated/"));
	}
}
