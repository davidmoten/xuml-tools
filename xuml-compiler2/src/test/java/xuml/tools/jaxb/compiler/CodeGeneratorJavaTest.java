package xuml.tools.jaxb.compiler;

import java.io.File;
import java.io.FileNotFoundException;

import miuml.jaxb.Marshaller;

import org.junit.Test;

public class CodeGeneratorJavaTest {

	@Test
	public void testCodeGenerationForABC() throws FileNotFoundException {
		generateClassesForDomain("Nested composite id example", "simple");
	}

	// @Test
	public void testCodeGenerationForBookstore() throws FileNotFoundException {
		generateClassesForDomain("Bookstore", "bookstore");
	}

	private void generateClassesForDomain(String domainName, String schema) {
		miuml.jaxb.Domains domains = new Marshaller().unmarshal(getClass()
				.getResourceAsStream("/samples.xml"));
		File resources = new File("target/generated-resources");
		if (!resources.exists())
			resources.mkdirs();
		new CodeGeneratorJava(domains, domainName, "miuml", schema, "miuml",
				resources).generate(new File("target/generated/"));
	}
}
