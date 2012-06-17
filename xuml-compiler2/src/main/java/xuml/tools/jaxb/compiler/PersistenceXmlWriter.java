package xuml.tools.jaxb.compiler;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class PersistenceXmlWriter {

	public String generate(List<String> classes) {
		StringBuilder s = new StringBuilder();
		for (String cls : classes)
			s.append("\t\t<class>" + cls + "</class>\n");
		try {
			String xml = IOUtils.toString(PersistenceXmlWriter.class
					.getResourceAsStream("/persistence-template.txt"));
			return xml.replace("CLASSES HERE", s.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
