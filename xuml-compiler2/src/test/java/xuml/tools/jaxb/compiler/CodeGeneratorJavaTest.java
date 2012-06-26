package xuml.tools.jaxb.compiler;

import java.io.File;
import java.io.FileNotFoundException;

import miuml.jaxb.Marshaller;
import miuml.jaxb.ModeledDomain;

import org.junit.Test;

public class CodeGeneratorJavaTest {

	@Test
	public void testCodeGenerationFromABC() throws FileNotFoundException {
		if ("false".equals(System.getProperty("generate")))
			return;
		miuml.jaxb.Domains domains = new Marshaller().unmarshal(getClass()
				.getResourceAsStream("/samples.xml"));
		File resources = new File("target/generated-resources");
		if (!resources.exists())
			resources.mkdirs();
		ModeledDomain abcDomain = Util.getModeledDomain(domains,
				"Nested composite id example");
		new CodeGeneratorJava(abcDomain, "miuml", "simple", "miuml", resources)
				.generate(new File("target/generated/"));
	}
}
