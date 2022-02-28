xuml-tools 
==========
<a href="https://github.com/davidmoten/xuml-tools/actions/workflows/ci.yml"><img src="https://github.com/davidmoten/xuml-tools/actions/workflows/ci.yml/badge.svg"/></a><br/>
[Executable UML](http://en.wikipedia.org/wiki/Executable_UML) tools based on the [miUML](http://www.miuml.org) [metamodel](https://docs.google.com/spreadsheet/ccc?key=0AtejhCC8R03tdERpSzJFdTRVWkFMYnN2MlZzbG5YYnc#gid=1) (thanks to Leon Starr!). 

* miUML metamodel XML schema
* Java model compiler (JSE/JEE)
* Javascript class diagram viewer
* Javascript state diagram viewer

The primary inspiration for this project is  [*Executable UML - A Foundation for Model Driven Architecture*](http://www.executableumlbook.com/) by Mellor &amp; Balcer.

Status: *released to Maven Central*

<img src="https://raw.github.com/davidmoten/xuml-tools/master/xuml-diagrams/src/docs/class-diagram.png">

Maven site is [here](http://davidmoten.github.io/xuml-tools/) including javadocs.

Technologies
-------------
Under the hood: 

* JPA is used for persistence and action language
* Akka is used for asynchronous signalling.

Getting started
---------------
Requirements:

* JDK 1.6 or later (for build, runtime)
* maven 3.0.3 or later (for build)

This is how to build the artifacts from source:

    git clone https://github.com/davidmoten/xuml-tools.git
    cd xuml-tools
    mvn clean install

### Worked example

See the example of [creating an Order Tracking system with a REST API](example.md).

Model schema
------------
Models are defined in xml that is compliant to the miUML xuml-tools [schema](https://github.com/davidmoten/xuml-tools/blob/master/miuml-jaxb/src/main/resources/miuml-metamodel.xsd). A sample of xml compliant with the schema is [samples.xml](https://github.com/davidmoten/xuml-tools/blob/master/miuml-jaxb/src/main/resources/samples.xml).

Java model compiler
-------------------
With the model compiler we seek to implement the approach taken by Mellor & Balcer in their super book [*Executable UML - A Foundation for Model Driven Architecture*](http://www.executableumlbook.com/). The most notable exception is using the Java Virtual Machine as the platform and Java as the Action Language (using BPAL97 equivalent commands listed below is encouraged).

<img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/xuml-model-compiler.png" width="600">

The java model compiler includes the following features (<img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">denotes done):

* Generates JPA entities  <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"> 
* Generates State Machine  <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"> 
* All association types
 * 1 to 1 <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * 0..1 to 1 <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"> 
 * 1 to * <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * 0..1 to * <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * 1 to 1..* <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * 0..1 to 1..* <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * * to * <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * * to * with Association Class <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * 1..* to * with Association Class <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * 1..* to 1..* with Association Class <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * Unary 1 <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * Unary 0..1 <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * Unary * <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * Unary * with Association Class
 * Unary 1..* <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * Unary 1..* with Association Class
* Event and State signatures <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
* Concise usage <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
* Composite primary identifiers  <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"> 
* Secondary identifiers as unique constraints <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"> 
* Specializations <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
* Signals to self buffered and executed within one transaction <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"> 
* Asynchronous persistent signalling  <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"> 
* Auto-detection of signals to self <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
* Custom exception handlers <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
* Uses Akka actors to handle concurrency <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
* Composite Id toString, equals and hashCode methods <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
* Entity toString method 
* Event toString method <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
* Generated find methods for attribute groups
* Composite Id Builder pattern <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
* Event Builder pattern  <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
* Domain and global type constraints honoured:
 * MaxLength <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * MinLength <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * Precision <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * LowerLimit <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * UpperLimit <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * Prefix <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * Suffix <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * ValidationPattern <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * DefaultValue  <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
* Database tests
 * Derby <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * H2 <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
 * HSQLDB
 * Postgres
 * MySQL
 * Oracle
 * DB2
 * SQL Server
* JPA Provider tests
 * Hibernate <img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png">
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

1. When a signal is sent to an entity (detected by the runtime) that signal is persisted synchronously to the signal table within a dedicated transaction and assigned a unique id. The signal is augmented with the unique id and passed asynchronously for processing. Control returns immediately to the signaller.
1. When the *Signal to other* for an entity is ready to be processed **a database transaction is started**. 
1. The relevant on-entry procedure is called in the entity's state machine.
1. If the on-entry procedure initiates a *Signal to self* that signal is added to a temporary *Signal to self* queue specific to the current transaction unless the signal to self is scheduled in the future (in which case it is treated like a *Signal to other*). 
1. If the on-entry procedure initiates a *Signal to other* that signal is added to a second temporary *Signal to other* queue specific to the current transaction. 
1. Once the on-entry-procedure completes, the queue of *Signal to self* is processed in arrival order.
1. The signal is then removed from the signal table (using the unique id assigned at time of sending). 
1. **Only then is the transaction committed**. 
1. If and only if the transaction succeeds the queue of *Signal to other* is processed (the signals are sent).

### Exception handling ###
The system should be developed and tested with the aim of no uncaught exceptions being thrown. However, unexpected exceptions need to be dealt with properly when they occur. For this purpose the developer may implement a [SignalProcessorListener](https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/main/java/xuml/tools/model/compiler/runtime/SignalProcessorListener.java) to perform retries, log/notify errors, or even halt processing on one or all entities.

An example of setting up a [SignalProcessorListenerFactory](https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/main/java/xuml/tools/model/compiler/runtime/SignalProcessorListenerFactory.java) and assigning it to the current Context is in [AbcTest.java](https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-test/src/test/java/xuml/tools/jaxb/compiler/test/AbcTest.java).

More examples:

<table>
  <tr><th>Purpose</th><th>Class</th></tr>
  <tr><td>Log failures</td><td><a href="https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/main/java/xuml/tools/model/compiler/runtime/SignalProcessorListenerUtilLogging.java">SignalProcessorListenerUtilLogging.java</a></td></tr>
  <tr><td>Retry on failure with 5 min delay</td><td><a href="https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/test/java/xuml/tools/model/compiler/runtime/SignalProcessorListenerRetryOnFailure.java">SignalProcessorListenerRetryOnFailure.java</a></td></tr>
  <tr><td>Stop all signal processing on failure</td><td><a href="https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/test/java/xuml/tools/model/compiler/runtime/SignalProcessorListenerStopsAllSignalProcessingOnFailure.java">SignalProcessorListenerStopsAllSignalProcessingOnFailure.java</a></td></tr>
  <tr><td>Stop signal processing on single entity on failure</td><td><a href="https://github.com/davidmoten/xuml-tools/blob/master/xuml-model-compiler-runtime/src/test/java/xuml/tools/model/compiler/runtime/SignalProcessorListenerStopsSignalProcessingSingleEntityOnFailure.java">SignalProcessorListenerStopsSignalProcessingSingleEntityOnFailure.java</a></td></tr>
</table>

### Action Language ###
The current plan is to make the semantics of say BPAL 97 (Bridgepoint Action Language used by examples in Mellor & Balcer) available in a concise form as methods on the generated java entities or as static utility methods. Examples are below:

<table>
	<tr><th>Action<th>BPAL97</th><th>Java</th><th>Status</th></tr>
	<tr><td><i>Create object</i></td><td><b>create object instance</b> customer <b>of</b> Customer;</td><td>Customer customer = Customer.create();</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Write attribute</i></td><td>customer.firstName = 'Dave';</td><td>customer.setFirstName("Dave");</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Read attribute</i></td><td>customer.firstName</td><td>customer.getFirstName()</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Delete object</i></td><td><b>delete object instance</b> customer;</td><td>customer.delete();</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
    <tr><td><i>Class extent</i></td><td><b>select many</b> customers <b>from instances of</b> Customer;</td><td>List&lt;Customer&gt; customers = Customer.select().many();</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
    <tr><td><i>Qualification (one object)</i></td><td><b>select any</b> customer <b>from instances of</b> Customer <b>where</b> selected.numPurchases>1 <b>and</b> selected.city='Canberra';</td><td>import static Customer.Attribute.*;<br/>Customer customer = Customer.select(numPurchases().gt(1).and(city().eq("Canberra")).any();</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
    <tr><td><i>Qualification (many objects)</i></td><td><b>select many</b> customers <b>from instances of</b> Customer <b>where</b> selected.numPurchases>1 <b>and</b> selected.city='Canberra';</td><td>List&lt;Customer&gt; customer = Customer.select(numPurchases().gt(1).and(city().eq("Canberra")).many();</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Create link</i></td><td><b>relate</b> customer <b>to</b> order <b>across</b> R1;</td><td>customer.relateAcrossR1(order);</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Traverse link</i></td><td><b>select many</b> order <b>related by</b> customer -&gt; Order[R1];</td><td>customer = order.getCustomer_R1();</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
	<tr><td><i>Delete link</i></td><td><b>unrelate</b> order <b>from </b> customer <b>across</b> R1;</td><td>customer.unrelateAcrossR1(order);</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
	<tr><td>Create link object</td><td><b>relate</b> author <b>to</b> book <b>across</b> R3 <b>creating</b> authorship;</td><td>Authorship authorship = author.relateAcrossR3(book);</td><td></td></tr>
	<tr><td>Traverse link</td><td><b>select one</b> authorship <b>that relates</b> author <b>to</b> book <b>across</b> R3</td><td>?</td><td></td></tr>
	<tr><td>Unrelate link</td><td><b>unrelate</b> author <b>from</b> book <b>across</b> R3</td><td>?</td><td></td></tr>
	<tr><td>Create specialization</td><td><b></b></td><td></td><td></td></tr>
	<tr><td>Reclassify specialization</td><td><b></b></td><td></td><td></td></tr>
	<tr><td>Delete specialization</td><td><b></b></td><td></td><td></td></tr>
	<tr><td>Generate signal</td><td><b>generate</b> addSelection <b>to</b> order;</td><td>order.signal(addSelection);</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
	<tr><td>Generate signal with delay</td><td><b>generate</b> addSelection <b>to</b> order <b>delay</b> 5 minutes</td><td>order.signal(addSelection, Duration.create(5,TimeUnit.MINUTES));</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
	<tr><td>Generate signal at time</td><td><b>generate</b> addSelection <b>to</b> order <b>at</b> <i>&lt;time&gt;</i></td><td>order.signal(addSelection, epochTimeMs);</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
	<tr><td>Generate signal with delay and repeat</td><td></td><td></td><td></td></tr>
	<tr><td>Cancel signal with delay</td><td><b>cancel</b> addSelection <b>from</b> customer <b>to</b> order</td><td>order.cancelSignal(addSelection);</td><td><img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/tick.png"></td></tr>
</table>

The principle is to write java on-entry methods using the above abstractions and without resorting to direct use of an *EntityManager*. The current *EntityManager* is always available via *Context.em()* but for simplicity and to maximize compile-time checking it is recommended to avoid using it. You might want to resort to using *Context.em()* for some performance tweak for example but try the *EntityManager*-free approach first.

Web Class Diagram Viewer
------------------------
Run it locally using Jetty. 

    cd xuml-tools/xuml-diagrams
    mvn clean jetty:run

Then open [http://localhost:8080/cd](http://localhost:8080/cd) in a browser.

Click on the open file button and upload both *domains.xml* and *domains.view* (if you have one) in the one selection. Drag items around till they look nice. Hit save button to download the new positions to *domains.view* (If you use chrome you might want to disable auto-download to a folder: **Settings** - **Show advanced settings** - **Downloads** - tick **Ask where to save each file before downloading**).

Web State Diagram Viewer
------------------------
Will look at this at some stage but no work done yet.
