xuml-tools 
==========

[Executable UML](http://en.wikipedia.org/wiki/Executable_UML) tools (xml schema, java model compiler, java+javascript model viewer) based on the [miUML](http://www.miuml.org) [metamodel](https://docs.google.com/spreadsheet/ccc?key=0AtejhCC8R03tdERpSzJFdTRVWkFMYnN2MlZzbG5YYnc#gid=1) (thanks to Leon Starr!).

This project is **in active development** and has not reached alpha release status yet (will be soon methinks).

<img src="https://github.com/davidmoten/xuml-tools/raw/master/xuml-diagrams/src/docs/class-diagram.png">

Continuous integration with Jenkins for this project is [here](https://xuml-tools.ci.cloudbees.com/).

<a href="https://xuml-tools.ci.cloudbees.com/"><img  src="http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png"/></a>

Maven site is [here](https://xuml-tools.ci.cloudbees.com/job/xuml-tools_site/site/). Includes [Javadocs](https://xuml-tools.ci.cloudbees.com/job/xuml-tools_site/site/apidocs/index.html), Cobertura test coverage, FindBugs, CPD, PMD, JavaNCSS, Checkstyle and other reports.

Getting started
---------------
Requirements:

* JDK 1.6 or later (for build, runtime)
* maven 3.0.3 or later (for build)

Until the project has matured enough to release artifacts to Maven Central repository this is how to locally install the artifacts from source:

    git clone https://github.com/davidmoten/xuml-tools.git
    cd xuml-tools
    mvn clean install

Once a model is specified in xml then xuml-tools generates a system (rich JPA classes), including a test harness with an in-memory generated database.

Use the maven archetype to create a project in interactive mode:
	
    cd <YOUR_WORKSPACE>
    mvn archetype:generate \
    -DarchetypeGroupId=org.github.davidmoten \
    -DarchetypeArtifactId=xuml-model-archetype \
    -DarchetypeVersion=0.0.1-SNAPSHOT

The generated project should build cleanly with *mvn clean install*. The build runs a simple unit test on the generated JPA classes using a temporary in-memory derby database. The JPA classes are generated from *src/main/resources/domain.xml* and the tests are in *src/test/java*.

Alternatively, look at the maven plugin below that you would insert into your pom.xml and follow your nose:
```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.github.davidmoten</groupId>
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

The module [**xuml-model-compiler-test**](https://github.com/davidmoten/xuml-tools/tree/master/xuml-model-compiler-test) demonstrates usage. In particular the test class [AbcTest.java](https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-test/src/test/java/xuml/tools/jaxb/compiler/test/AbcTest.java) demonstrates normal system lifecycle.



Model schema
------------
Models are defined in xml that is compliant to the miUML xuml-tools [schema](https://github.com/davidmoten/xuml-tools/blob/master/miuml-jaxb/src/main/resources/miuml-metamodel.xsd). A sample of xml compliant with the schema is [samples.xml](https://github.com/davidmoten/xuml-tools/blob/master/miuml-jaxb/src/main/resources/samples.xml).

Java model compiler
-------------------
With the model compiler we seek to implement the approach taken by Mellor & Balcer in their super book [*Executable UML - A Foundation for Model Driven Architecture*](http://www.executableumlbook.com/). The most notable exception is the Action Language which is java.

<img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/xuml-model-compiler.png" width="600">

The java model compiler includes the following features (<img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">denotes done):
* Generates JPA entities  <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"> 
* Generates State Machine  <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"> 
* All association types
 * 1 to 1 <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * 0..1 to 1 <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"> 
 * 1 to * <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * 0..1 to * <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * 1 to 1..* <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * 0..1 to 1..* <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * * to * <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * * to * with Association Class <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * 1..* to * with Association Class 
 * Unary 1 <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * Unary 0..1 <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * Unary * <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * Unary * with Association Class
 * Unary 1..* <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * Unary 1..* with Association Class
* Event and State signatures <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* Concise usage <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* Composite primary identifiers  <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"> 
* Secondary identifiers as unique constraints <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"> 
* Specializations <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* Signals to self buffered and executed within one transaction <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"> 
* Asynchronous persistent signalling  <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"> 
* Auto-detection of signals to self <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* Uses Akka actors to handle concurrency <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* Generated toString, equals and hashCode methods for composite ids <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* Generated toString method for entities
* Generated toString method for events
* Generated find methods for attribute groups
* Domain and global type constraints honoured:
 * MaxLength <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * MinLength <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * Precision <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * LowerLimit <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * UpperLimit <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * Prefix <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * Suffix <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * ValidationPattern <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * DefaultValue

The todo list includes:
* Event polymorphism
* Comprehensive unit testing
* Synchronous signalling (?) 
* Integration with other products (OpenXava?)

The items that may be left out:
* Structured types
* Bridges
* Derived attributes 
* any specific Action Language implementation 

Web Class Diagram Viewer
------------------------
These pre-alpha demos are available:

* [CloudBees](http://xuml-diagrams.xuml-tools.cloudbees.net/) using in-memory datastore
* [Google App Engine](http://xuml-tools.appspot.com) using Big Table datastore

Run it locally using Jetty and in-memory datastore (for saving diagram positions):

    cd xuml-tools/xuml-diagrams
    mvn clean jetty:run

Or run locally using Google App Engine (takes much longer to startup). Note: uses in-memory datastore at the moment.

    cd xuml-tools/xuml-diagrams
    mvn gae:unpack 
    mvn clean gae:run
    
Note that gae:unpack need only be run once to download the sdk.

Then open [http://localhost:8080]() in a browser.

Web State Diagram Viewer
------------------------
Will look at this at some stage but no work done yet.
