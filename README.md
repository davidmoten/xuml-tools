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

The module [**xuml-model-compiler-test**](https://github.com/davidmoten/xuml-tools/tree/master/xuml-model-compiler-test) tests all association types and demonstrates usage. In particular the test class [AbcTest.java](https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-test/src/test/java/xuml/tools/jaxb/compiler/test/AbcTest.java) demonstrates normal system lifecycle.



Model schema
------------
Models are defined in xml that is compliant to the miUML xuml-tools [schema](https://github.com/davidmoten/xuml-tools/blob/master/miuml-jaxb/src/main/resources/miuml-metamodel.xsd). A sample of xml compliant with the schema is [samples.xml](https://github.com/davidmoten/xuml-tools/blob/master/miuml-jaxb/src/main/resources/samples.xml).

Java model compiler
-------------------
With the model compiler we seek to implement the approach taken by Mellor & Balcer in their super book [*Executable UML - A Foundation for Model Driven Architecture*](http://www.executableumlbook.com/). The most notable exception is using Java as the Action Language.

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
 * 1..* to * with Association Class <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * 1..* to 1..* with Association Class <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
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
* Composite Id toString, equals and hashCode methods <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* Entity toString method 
* Event toString method <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* Generated find methods for attribute groups
* Composite Id Builder pattern <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* Event Builder pattern  <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
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
* Database tests
 * Derby <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * Postgres
 * MySQL
 * Oracle
 * H2
 * DB2
 * Hypersonic
 * SQL Server
* JPA Provider tests
 * Hibernate <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * EclipseLink
 * TopLink
 * OpenJPA

The todo list includes:
* Event polymorphism
* Comprehensive unit testing
* Synchronous signalling (?) 
* Integration with other products 
 * GUI generation (OpenXava?)

The items that may be left out:
* Structured types
* Bridges
* Derived attributes 
* any specific Action Language implementation 

### Transactions ###
The xuml-model-compiler runtime takes the following approach in terms of database transactions:

There are two types of signals:
* *Signal to self* (initiated by an entity's on entry procedure to itself)
* *Signal to other* 

In terms of the role transactions play in relation to signals:

1. When a *Signal to other* is made to an entity it is added to the signal queue for that entity to be processed in order of arrival.
1. When the *Signal to other* for an entity is ready to be processed **a database transaction is started**. 
1. The signal is persisted to the signal table.
1. The appropriate on-entry procedure is called in the entity's state machine.
1. If the on-entry procedure initiates a *Signal to self* that signal is added to a temporary *Signal to self* queue specific to the current transaction. 
1. If the on-entry procedure initiates a *Signal to other* that signal is added to a second temporary *Signal to other* queue specific to the current transaction. 
1. Once the on-entry-procedure completes, the queue of *Signal to self* is processed in arrival order.
1. The signal is then removed from the signal table. 
1. **Only then is the transaction committed**. 
1. If and only if the transaction succeeds the queue of *Signal to other* is processed (the signals are sent).

### Exception handling ###
The system should be developed and tested with the aim of no uncaught exceptions being thrown.

If an exception occurs during the processing of a transaction then

1. The transaction is rolled back and processing of the signal is aborted.
1. An error is optionally written to the application log (not implemented yet).
1. The signal that prompted the exception remains in the persisted signals table and the number of failures of that signal is incremented in the table.
1. Other signals continue processing as normal (if the system is configured to allow this).

The treatment of failing signals is up to the developer. The developer may wish to periodically reprocess the messages in the queue (call Context.sendSignalsInQueue()) and perhaps when the number of failures or time since first failure reaches a certain level some investigative action may be prompted. Given that the system was developed to not throw uncaught exceptions it's probable that any failed signal requires investigation on the part of the developer.

It is possible that the developer may wish to halt all processing of signals on the first failed transaction. Some configuration or notification mechanism will be included in xuml-model-compiler-runtime to enable this.

Web Class Diagram Viewer
------------------------
The following examples are based on storing the domain xml and the associated presentation settings on the server. To be investigated is the http://www.diagram.ly approach (see this [interview](http://doeswhat.com/2011/04/11/interview-with-david-benson-diagramly/)) where all user data is stored on the client machine and the application does no account management. Might be worth pursuing.

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
