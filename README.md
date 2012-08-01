xuml-tools 
==========

[Executable UML](http://en.wikipedia.org/wiki/Executable_UML) tools based on the [miUML](http://www.miuml.org) [metamodel](https://docs.google.com/spreadsheet/ccc?key=0AtejhCC8R03tdERpSzJFdTRVWkFMYnN2MlZzbG5YYnc#gid=1) (thanks to Leon Starr!).

* miUML metamodel XML schema
* Java model compiler (JSE/JEE)
* Javascript class diagram viewer
* Javascript state diagram viewer

This project is **in active development** and has not reached alpha release status yet (will be soon methinks).

<img src="https://github.com/davidmoten/xuml-tools/raw/master/xuml-diagrams/src/docs/class-diagram.png">

Continuous integration with Jenkins for this project is [here](https://xuml-tools.ci.cloudbees.com/). <a href="https://xuml-tools.ci.cloudbees.com/"><img  src="http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png"/></a>

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
* Custom exception handlers <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
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
 * H2 <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
 * HSQLDB
 * Postgres
 * MySQL
 * Oracle
 * DB2
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
* Extensions for binding with existing databases (to override default generated JPA annotations).
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

1. When a signal is sent to another entity (detected by the runtime) that signal is persisted synchronously to the signal table within a dedicated transaction and assigned a unique id. The signal is augmented with the unique id and passed asynchronously for processing. Control returns immediately to the signaller.
1. When the *Signal to other* for an entity is ready to be processed **a database transaction is started**. 
1. The relevant on-entry procedure is called in the entity's state machine.
1. If the on-entry procedure initiates a *Signal to self* that signal is added to a temporary *Signal to self* queue specific to the current transaction. 
1. If the on-entry procedure initiates a *Signal to other* that signal is added to a second temporary *Signal to other* queue specific to the current transaction. 
1. Once the on-entry-procedure completes, the queue of *Signal to self* is processed in arrival order.
1. The signal is then removed from the signal table (using the unique id assigned at time of sending). 
1. **Only then is the transaction committed**. 
1. If and only if the transaction succeeds the queue of *Signal to other* is processed (the signals are sent).

### Exception handling ###
The system should be developed and tested with the aim of no uncaught exceptions being thrown. However, unexpected exceptions need to be dealt with properly when they occur. For this purpose the developer may implement an [EntityActorListener](https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/main/java/xuml/tools/model/compiler/runtime/actor/EntityActorListener.java) to perform retries, log/notify errors, or even halt processing on one or all entities.

An example of setting up an [EntityActorListenerFactory](https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/main/java/xuml/tools/model/compiler/runtime/actor/EntityActorListenerFactory.java) and assigning it to the current Context is in [AbcTest.java](https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-test/src/test/java/xuml/tools/jaxb/compiler/test/AbcTest.java).

More examples:

<table>
  <tr><th>Purpose</th><th>Class</th></tr>
  <tr><td>Log failures</td><td><a href="https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/test/java/xuml/tools/model/compiler/runtime/actor/EntityActorListenerLogging.java">EntityActorListenerLogging.java</a></td></tr>
  <tr><td>Retry on failure with 5 min delay</td><td><a href="https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/test/java/xuml/tools/model/compiler/runtime/actor/EntityActorListenerRetryOnFailure.java">EntityActorListenerRetryOnFailure.java</a></td></tr>
  <tr><td>Stop all signal processing on failure</td><td><a href="https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/test/java/xuml/tools/model/compiler/runtime/actor/EntityActorListenerStopsAllSignalProcessingOnFailure.java">EntityActorListenerStopsAllSignalProcessingOnFailure.java</a></td></tr>
  <tr><td>Stop signal processing on single entity on failure</td><td><a href="https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/test/java/xuml/tools/model/compiler/runtime/actor/EntityActorListenerStopsSignalProcessingSingleEntityOnFailure.java">EntityActorListenerStopsSignalProcessingSingleEntityOnFailure.java</a></td></tr>
</table>

### Action Language ###
The current plan is to make the semantics of say BPAL 97 (Bridgepoint Action Language used by examples in Mellor & Balcer) available in a concise form as methods on the generated java entities or as static utility methods. The desired functionality includes:

* relate to across relationship <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* select any,one,many with where clause <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* select using association class
* create methods <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* deletion methods
* generate methods <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">
* property setters and getters <img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png">

<table>
	<tr><th>Action<th>BPAL97</th><th>Java</th><th>Status</th></tr>
	<tr><td><i>Create object</i></td><td><b>create object instance</b> customer <b>of</b> Customer;</td><td>Customer customer = Customer.create();</td><td><img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Write attribute</i></td><td>customer.firstName = 'Dave';</td><td>customer.setFirstName("Dave");</td><td><img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Read attribute</i></td><td>customer.firstName</td><td>customer.getFirstName()</td><td><img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Delete object</i></td><td><b>delete object instance</b> customer;</td><td>customer.delete();</td><td></td></tr>
    <tr><td><i>Class extent</i></td><td><b>select many</b> customers <b>from instances of</b> Customer;</td><td>List&lt;Customer&gt; customers = Customer.select().many();</td><td><img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"></td></tr>
    <tr><td><i>Qualification (one object)</i></td><td><b>select any</b> customer <b>from instances of</b> Customer <b>where</b> selected.numPurchases>1 <b>and</b> selected.city='Canberra';</td><td>Customer customer = Customer.select(numPurchases().gt(1).and(city().eq("Canberra")).any();</td><td><img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"></td></tr>
    <tr><td><i>Qualification (many objects)</i></td><td><b>select many</b> customers <b>from instances of</b> Customer <b>where</b> selected.numPurchases>1 <b>and</b> selected.city='Canberra';</td><td>List&lt;Customer&gt; customer = Customer.select(numPurchases().gt(1).and(city().eq("Canberra")).many();</td><td><img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Create link</i></td><td><b>relate</b> customer <b>to</b> order <b>across</b> R1;</td><td>customer.relateAcrossR1(order);</td><td><img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Traverse link</i></td><td><b>select many</b> order <b>related by</b> customer -&gt; Order[R1];</td><td>customer = order.getCustomer_R1();</td><td><img src="https://github.com/davidmoten/xuml-tools/raw/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Delete link</i></td><td><b>unrelate</b> order <b>from </b> customer <b>across</b> R1;</td><td>customer.unrelateAcrossR1(order);</td><td></td></tr>
	<tr><td>Create link object</td><td><b>relate customer to product across R2</b><td><td></td><td></td></tr>
	<tr><td>Traverse link</td><td><b></b><td><td></td><td></td></tr>
	<tr><td>Unrelate link</td><td><b></b><td><td></td><td></td></tr>
	<tr><td>Create specialization</td><td><b></b><td><td></td><td></td></tr>
	<tr><td>Reclassify specialization</td><td><b></b><td><td></td><td></td></tr>
	<tr><td>Delete specialization</td><td><b></b><td><td></td><td></td></tr>
</table>

The principle is to write java on-entry methods using the above abstractions without resorting to direct use of an *EntityManager*. The current *EntityManager* is always available via *Context.em()* but for simplicity and to maximize compile-time checking it is recommended to avoid using it. You might want to resort to using *Context.em()* for some performance tweak for example but try the *EntityManager*-free approach first.

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
