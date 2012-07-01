xuml-tools
==========

Executable UML tools (xml schema, java model compiler, javascript model viewer) based on miUML metamodels.

Getting started
---------------
Until the project has matured enough to release artifacts to Maven Central repository this is how to locally install the artfacts from source:

    git clone https://github.com/davidmoten/xuml-tools.git
    cd xuml-tools
    mvn clean install

To generate your own JPA classes from xml compliant with the miUML schema add the following plugin to your pom.xml:
```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.moten.david</groupId>
			<artifactId>xuml-tools-maven-plugin</artifactId>
			<version>0.0.1-SNAPSHOT</version>
			<executions>
				<execution>
					<id>generate-jpa</id>
					<goals>
						<goal>generate-jpa</goal>
					</goals>
					<configuration>
						<domainsXml>/samples.xml</domainsXml>
						<domain>Nested composite id example</domain>
						<schema>abc</schema>
						<packageName>abc</packageName>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

Java model compiler
-------------------
The java model compiler includes the following features from Executable UML:

* Composite primary keys
* Asynchronous signalling
* Auto-detection of signals to self

The todo list includes:
* Event polymorphism
* Comprehensive unit testing
* Persistent signalling 

The items that may be left out:
* Structured types
* Bridges
* any specific Action Language implementation 

Web Class Diagram Viewer
------------------------

    cd xuml-tools/xuml-diagrams
    mvn clean gae:run
Then open (http://localhost:8080) in a browser.