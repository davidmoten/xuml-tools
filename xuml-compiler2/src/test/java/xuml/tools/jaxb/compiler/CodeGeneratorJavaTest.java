package xuml.tools.jaxb.compiler;

import java.io.File;
import java.io.FileNotFoundException;

import miuml.jaxb.Marshaller;
import miuml.jaxb.ModeledDomain;

import org.junit.Test;

public class CodeGeneratorJavaTest {

	@Test
	public void test() throws FileNotFoundException {
		if ("false".equals(System.getProperty("generate")))
			return;
		miuml.jaxb.Domains domains = new Marshaller().unmarshal(getClass()
				.getResourceAsStream("/bookstore.xml"));
		File resources = new File("target/generated-resources");
		if (!resources.exists())
			resources.mkdirs();
		new CodeGeneratorJava((ModeledDomain) domains.getDomain().get(2)
				.getValue(), "miuml", "miuml", resources).generate(new File(
				"target/generated/"));
	}
}
