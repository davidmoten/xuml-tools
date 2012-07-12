package xuml.tools.jaxb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBElement;

import miuml.jaxb.Domain;
import miuml.jaxb.Domains;
import miuml.jaxb.Marshaller;
import miuml.jaxb.ModeledDomain;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ClassDiagramGeneratorTest {

	@Test
	public void testGenerate() throws IOException {
		Domains domains = new Marshaller().unmarshal(getClass()
				.getResourceAsStream("/samples.xml"));
		File webapp = new File("target/webapp");
		FileUtils.deleteDirectory(webapp);
		FileUtils.copyDirectoryToDirectory(new File("src/main/webapp"),
				new File("target"));
		for (JAXBElement<? extends Domain> element : domains.getDomain()) {
			if (element.getValue() instanceof ModeledDomain) {
				String domainName = element.getValue().getName();
				generateFromSample(
						domains,
						domainName,
						"target/webapp/diagram-"
								+ domainName.replaceAll(" ", "-") + ".html");
			}
		}
	}

	public void generateFromSample(Domains domains, String domainName,
			String filename) {
		System.out.println("generating domain " + domainName + " to "
				+ filename);
		ClassDiagramGenerator g = new ClassDiagramGenerator();
		String s = g.generate(domains, domainName);
		System.out.println(s);
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			IOUtils.copy(IOUtils.toInputStream(s), fos);
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
