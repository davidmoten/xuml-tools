package xuml.tools.diagram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.google.common.base.Optional;

import jakarta.xml.bind.JAXBElement;
import xuml.tools.miuml.metamodel.jaxb.Domain;
import xuml.tools.miuml.metamodel.jaxb.Domains;
import xuml.tools.miuml.metamodel.jaxb.Marshaller;
import xuml.tools.miuml.metamodel.jaxb.ModeledDomain;
import xuml.tools.miuml.metamodel.jaxb.Subsystem;

public class ClassDiagramGeneratorTest {

    @Test
    public void testGenerate() throws IOException {
        Domains domains = new Marshaller()
                .unmarshal(getClass().getResourceAsStream("/samples.xml"));
        File webapp = new File("target/webapp");
        FileUtils.deleteDirectory(webapp);
        FileUtils.copyDirectoryToDirectory(new File("src/main/webapp"), new File("target"));
        for (JAXBElement<? extends Domain> element : domains.getDomain()) {
            if (element.getValue() instanceof ModeledDomain) {
                ModeledDomain d = (ModeledDomain) element.getValue();
                String domainName = element.getValue().getName();
                for (Subsystem ss : d.getSubsystem())
                    generateFromSample(domains, domainName, ss.getName(),
                            "target/webapp/diagram_" + domainName.replaceAll(" ", "-") + "_"
                                    + ss.getName().replaceAll(" ", "-") + ".html");
            }
        }
    }

    public void generateFromSample(Domains domains, String domainName, String subsystemName,
            String filename) {
        System.out.println("generating domain " + domainName + " to " + filename);
        ClassDiagramGenerator g = new ClassDiagramGenerator();
        String s = g.generate(domains, domainName, subsystemName, Optional.<String> absent());
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
