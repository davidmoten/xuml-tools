package ${package};

import java.io.File;

import xuml.tools.miuml.metamodel.jaxb.Domains;
import xuml.tools.miuml.metamodel.jaxb.Marshaller;
import xuml.tools.model.compiler.CodeGeneratorJava;

public class GeneratorMain {

    public static void main(String[] args) {

        Domains domains = new Marshaller()
                .unmarshal(GeneratorMain.class.getResourceAsStream("/domains.xml"));

        String domain = "test-domain";
        String packageName = "test";
        String schema = "test_domain";
        File outputSourceDirectory = new File("target/generated-sources");
        File resourcesDirectory = new File("target/generated-resources");
        String implementationPackageName = "test";
        File implementationSourceDirectory = new File("target/generated-sources");
        boolean generatePersistenceXml = false;
        boolean implementationOverwrite = true;
        new CodeGeneratorJava(domains, domain, packageName, schema, outputSourceDirectory,
                resourcesDirectory, implementationPackageName, implementationSourceDirectory,
                generatePersistenceXml, implementationOverwrite).generate();
    }

}
