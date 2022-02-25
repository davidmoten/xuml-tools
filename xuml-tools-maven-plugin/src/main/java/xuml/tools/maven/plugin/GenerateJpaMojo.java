package xuml.tools.maven.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Optional;

import jakarta.xml.bind.JAXBElement;
import xuml.tools.diagram.ClassDiagramGenerator;
import xuml.tools.miuml.metamodel.jaxb.Domain;
import xuml.tools.miuml.metamodel.jaxb.Marshaller;
import xuml.tools.miuml.metamodel.jaxb.ModeledDomain;
import xuml.tools.miuml.metamodel.jaxb.Subsystem;
import xuml.tools.model.compiler.CodeGeneratorJava;

/**
 * Generates JPA classes from a miUML schema specified domain.
 * 
 * @goal generate-jpa
 * 
 * @phase process-resources
 */
public class GenerateJpaMojo extends AbstractMojo {

    private static final String DEFAULT_IMPLEMENTATION_SUB_PACKAGE = "behaviour";

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     * @since 1.0
     */
    private MavenProject project;

    /**
     * Location to place generated java source.
     * 
     * @parameter default-value="${project.build.directory}/generated-sources"
     */
    private File outputSourceDirectory;

    /**
     * Location of miUML schema compliant xml (classpath checked first then
     * filesystem)
     * 
     * @parameter default-value="/domains.xml"
     * @required
     */
    private String domainsXml;

    /**
     * Specific domain in domains xml to generate from.
     * 
     * @parameter default-value="/domains.xml"
     * @required
     */
    private String domain;

    /**
     * Schema name.
     * 
     * @parameter default-value="xuml"
     * @required
     */
    private String schema;

    /**
     * Package name for generated implementation source. Defaults to packageName
     * + ".behaviour".
     * 
     * @parameter
     */
    private String implementationPackageName;

    /**
     * Implementation generated source directory.
     * 
     * @parameter default-value="${project.build.directory}/generated-sources"
     */
    private File implementationSourceDirectory;

    /**
     * Implementation generated source directory.
     * 
     * @parameter default-value="false"
     */
    private boolean implementationOverwrite;

    /**
     * Resources directory.
     * 
     * @parameter default-value="${project.build.directory}/generated-resources"
     */
    private File resourcesDirectory;

    /**
     * If and only if true generate META-INF/persistence.xml in
     * resourcesDirectory.
     * 
     * @parameter default-value="true"
     */
    private boolean generatePersistenceXml;

    /**
     * Root package name of the generated classes.
     * 
     * @parameter default-value="xuml"
     * @required
     */
    private String packageName;

    @Override
    public void execute() throws MojoExecutionException {

        createDirectory(outputSourceDirectory);
        createDirectory(implementationSourceDirectory);

        xuml.tools.miuml.metamodel.jaxb.Domains domains = getDomains();

        if (implementationPackageName == null)
            implementationPackageName = packageName + "." + DEFAULT_IMPLEMENTATION_SUB_PACKAGE;

        generate(domains);

        project.addCompileSourceRoot(outputSourceDirectory.getAbsolutePath());
        // TODO add resourcesDirectory to resources

        generateClassDiagrams(domains);
    }

    private void generate(xuml.tools.miuml.metamodel.jaxb.Domains domains) {
        new CodeGeneratorJava(domains, domain, packageName, schema, outputSourceDirectory,
                resourcesDirectory, implementationPackageName, implementationSourceDirectory,
                generatePersistenceXml, implementationOverwrite).generate();
    }

    private void generateClassDiagrams(xuml.tools.miuml.metamodel.jaxb.Domains domains) {
        String localRepo = project.getProperties().getProperty("settings.localRepository");

        @SuppressWarnings("unchecked")
        Set<Artifact> artifacts = project.getDependencyArtifacts();
        for (Artifact artifact : artifacts) {
            if (artifact.getArtifactId().equals("xuml-diagrams")
                    && artifact.getGroupId().equals("org.github.davidmoten")) {
                String version = artifact.getVersion();
                String name = "org/github/davidmoten/xuml-diagrams/" + version + "/xuml-diagrams-"
                        + version + ".war";
                File war = new File(localRepo, name);
                getLog().info("found war: " + war.getAbsolutePath());
            }
        }

        int domainIndex = 0;
        for (JAXBElement<? extends Domain> domain : domains.getDomain()) {
            if (domain.getValue() instanceof ModeledDomain) {
                ModeledDomain md = (ModeledDomain) domain.getValue();
                int ssIndex = 0;
                for (@SuppressWarnings("unused")
                Subsystem ss : md.getSubsystem()) {
                    String s = new ClassDiagramGenerator().generate(domains, domainIndex, ssIndex,
                            Optional.<String> absent());
                    String name = md.getName().replaceAll(" ", "_") + "_" + ssIndex + ".html";
                    writeToFile(resourcesDirectory, name, s);
                    ssIndex++;
                }
            }
            domainIndex++;
        }
    }

    private xuml.tools.miuml.metamodel.jaxb.Domains getDomains() throws MojoExecutionException {
        InputStream is = getClass().getResourceAsStream(domainsXml);
        if (is == null)
            try {
                getLog().info("domains xml file not found on classpath: " + domainsXml);
                File file = new File(domainsXml);
                if (!file.exists())
                    file = new File(project.getBasedir(), domainsXml);
                getLog().info("loading domains xml from file system: " + file.getCanonicalPath());
                is = new FileInputStream(file);
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        xuml.tools.miuml.metamodel.jaxb.Domains domains = new Marshaller().unmarshal(is);
        return domains;
    }

    private void createDirectory(File directory) {
        if (!directory.exists()) {
            if (directory.mkdirs())
                getLog().info("created directory " + directory);
        }
    }

    private void writeToFile(File resources, String name, String html) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(resources, name));
            fos.write(html.getBytes());
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
