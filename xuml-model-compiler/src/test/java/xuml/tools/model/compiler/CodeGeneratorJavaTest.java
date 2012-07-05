package xuml.tools.model.compiler;

import java.io.File;
import java.io.FileNotFoundException;

import miuml.jaxb.Marshaller;

import org.junit.Test;

public class CodeGeneratorJavaTest {

	@Test
	public void testCodeGenerationForABC() throws FileNotFoundException {
		generateClassesForDomain("Nested composite id example", "miuml",
				"simple");
	}

	// @Test
	public void testCodeGenerationForBookstore() throws FileNotFoundException {
		generateClassesForDomain("Bookstore", "bookstore", "bookstore");
	}

	@Test
	public void testCodeGenerationForOneToZeroOne()
			throws FileNotFoundException {
		generateClassesForDomain("one-to-zero-one", "one_to_zero_one",
				"one_to_zero_one");
	}

	@Test
	public void testCodeGenerationForOneToOne() throws FileNotFoundException {
		generateClassesForDomain("one-to-one", "one_to_one", "one_to_one");
	}

	private void generateClassesForDomain(String domainName,
			String domainPackageName, String schema) {
		miuml.jaxb.Domains domains = new Marshaller().unmarshal(getClass()
				.getResourceAsStream("/samples.xml"));
		File resources = new File("target/generated-resources");
		if (!resources.exists())
			resources.mkdirs();
		new CodeGeneratorJava(domains, domainName, domainPackageName, schema)
				.generate(new File("target/generated/"));
	}
}
