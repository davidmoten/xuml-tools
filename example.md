Implementing an Order Tracker using xuml-tools
===============================================

The increasing popularity of microservices is a perfect opportunity to make the most of Executable UML in small self-contained systems with their own relational database for persistence and perhaps a service offering e.g. REST to allow a bridge to other subsystems.

Let's implement an Order Tracker subsystem with a REST API using *xuml-tools*.

The Order Tracker
-------------------
The idea for this subsystem is that an order (like say an online purchase) is:

* prepared for dispatching (the components of the order gathered up and packaged)
* picked up by a courier
* transited to the depot closest to the destination
* delivery is attempted to a destination multiple times
* once max delivery attempts have occurred the item is held at a nearby depot for the client to pick up
* the item would be held for a maximum period (say 14 days) before being returned to sender

The entities involved are 

**Order** - orderId, description, fromAddress, toAddress, destinationEmail, senderEmail, lastDepotId, maxAttempts, attempts, comment

**Depot** - depotId, name, lat, long

The states for *Order* are:

* *Preparing*
* *Ready For dispatch*
* *Courier assigned*
* *In transit*
* *Ready For delivery*
* *Delivering*
* *Delivered*
* *Awaiting next delivery attempt*
* *Held for pickup*
* *Could not deliver*
* *Return to sender*

The transitions are:

* *Preparing* -> *Ready for dispatch* : **Send**
* *Ready for dispatch* -> *Courier assigned* : **Assign**
* *Courier assigned* -> *In transit* : **Picked up**
* *In transit* -> *In transit* : **Arrived depot**
* *In transit* -> *Ready for delivery* : **Arrived final depot**
* *Ready for delivery* -> *Delivering* : **Delivering**
* *Delivering* -> *Delivered* : **Delivered**
* *Delivering* -> *Awaiting next delivery attempt* : **Delivery failed** 
* *Awaiting next delivery attempt* -> *Ready for delivery* : **Deliver again** (delay till next day)
* *Ready for delivery* -> *Held for pickup* : **No more attempts**
* *Held for pickup* -> *Delivered* : **Delivered by pickup**
* *Delivering* -> *Could not deliver* : **Could not deliver**
* *Held for pickup* -> *Return to sender* : **Return to sender** (delay 14 days)

API interactions
------------------
The following interactions might occur with the API.

* An *Ordering system* would interact with the *Order Tracker* by creating an *Order* and then signalling *Order* instances with events using the *Order Traffic* Rest API.
* The *Ordering system* might schedule deliveries from each depot each day by asking the *Order Tracker* system for all orders in state *Ready for delivery* at the depot.
* The *Ordering system* might also schedule pickups from senders by requesting all orders in state *Ready for dispatch* to then assign to a courier.
* A recipient might want to know the current location of an order.

Where do we start?
-------------------

### Create a project
Create a project using the archetype:

```bash
mvn archetype:generate \
-DarchetypeGroupId=com.github.davidmoten \
-DarchetypeArtifactId=xuml-model-archetype \
-DarchetypeVersion=0.1-SNAPSHOT \
-DgroupId=my.stuff \
-DartifactId=order.tracker \
-Dversion=0.1-SNAPSHOT \
-DinteractiveMode=false
```

### Specify the model
The next step is to transfer what we know about the classes, attributes, relationships, states and transitions of the Order Tracker subsystem to the *src/main/resources/domains.xml* file based on the miUML schema.

Let's start small and add the *Order* class with its identifier attribute and nothing else:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<Domains xmlns="http://www.miuml.org/metamodel" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.miuml.org/metamodel https://raw.github.com/davidmoten/xuml-tools/master/miuml-jaxb/src/main/resources/miuml-metamodel.xsd  http://org.github/xuml-tools/miuml-metamodel-extensions https://raw.github.com/davidmoten/xuml-tools/master/miuml-jaxb/src/main/resources/xuml-tools-miuml-metamodel-extensions.xsd"
    xmlns:xt="http://org.github/xuml-tools/miuml-metamodel-extensions">

    <ModeledDomain Name="Ordering">
        <SymbolicType Name="OrderID" Prefix="" Suffix="" ValidationPattern=".*"
            DefaultValue="" MinLength="1" MaxLength="2048" />
        <Subsystem Name="OrderTracker" Floor="1" Ceiling="20">
            <Class Name="Order">
                <IndependentAttribute Name="Order ID" Type="OrderID">
                    <Identifier Number="1" />
                </IndependentAttribute>
            </Class>
        </Subsystem>
    </ModeledDomain>

</Domains>
```

If you use a featured xml editor like the Eclipse XML Editor (installed with the Web Tools Platform (WTP)) then you get auto-complete and validation as you type. Just by using xml and xsd we get an editor without having to write one ourselves (albeit without pretty pictures!).

### Customize the project

Edit pom.xml and set the configuration of *xuml-tools-maven-plugin* so it has these corrections:

```xml
<domain>Ordering</domain>
<schema>ordertracker</schema>
<packageName>ordertracker</packageName>
```

Also edit *src/test/resources/META-INF/persistence.xml* and ensure the class generated from *Order* is listed:

```xml
...
<persistence-unit name="testPersistenceUnit">
	<class>ordertracker.Order</class>
	<class>xuml.tools.model.compiler.runtime.QueuedSignal</class>
	<exclude-unlisted-classes>true</exclude-unlisted-classes>
	...
```

Run

    mvn clean test

You should get a couple of compile errors. 

Edit *src/main/java/my/stuff/App.java* and fix the import of *Context* so that it comes from the package *ordertracker*.

Then this should succeed:

    mvn clean test

### Complete the model

Now fill in the details of *domain.xml* and the result will be something like [this](order-tracker/src/main/resources/domains.xml).

### View the class diagram

```bash
cd xuml-diagrams
mvn jetty:run
```

Go to [http://localhost:8080/cd?id=1](http://localhost:8080/cd?id=1) and click on **Choose file**. Select your domains.xml file and the first domain in the file will be loaded into the Class Diagram Viewer. Drag classes around till it looks nice.

When *domains.xml* is loaded into the Class Diagram Viewer we get:

<img src="https://raw.github.com/davidmoten/xuml-tools/master/src/docs/class-diagram-order-tracker.png">

### Add behaviour using Java
The next step with Executable UML is detailing the actions that occur when a state transition occurs.

Ideally we would use a dedicated action language (like BPAL97) and an editor that supports it to create the actions. However, this is hard work from a tooling perspective and limits adoption by companies that are don't want to fork off into separate platforms and languages. If you don't mind using Java (or another jvm language) then *xuml-tools* offers the terrific advantages of Executable UML without the platform independence.

A plus for allowing java as the action language is that we have a very natural interaction with the database via JPA. No sql is written in the actions (although you can if you really want to). Test databases with fully instantiated schemas are created automatically by the JPA provider if desired just by adding an option to *persistence.xml*.

We are going to use java for the action language but it is recommended that the conventions of BPAL97 are adhered to, albeit with different syntax. For a java programmer this is a very convenient and comfortable place to be.

Because *Order* has a state machine we need to specify the actions for *Order*. Implement the class *OrderBehaviour* in the project *order-tracking* like [this](order-tracker/src/main/java/ordertracker/OrderBehaviour.java). 

Note that the [*OrderBehaviour*](order-tracker/src/main/java/ordertracker/OrderBehaviour.java) class also contains a static ```createFactory``` method which is used at startup (see [App.java](order-tracker/src/main/java/ordertracker/App.java)). 
 
For example, to implement the rule that an item is returned to sender after being held for pickup for 14 days:

```java
package ordertracker;

import java.util.concurrent.TimeUnit;

import ordertracker.Order.Behaviour;
import ordertracker.Order.Events.CouldNotDeliver;
import ordertracker.Order.Events.NoMoreAttempts;
import ordertracker.Order.Events.ReturnToSender;
import scala.concurrent.duration.Duration;

public class OrderBehaviour implements Order.Behaviour {

	private final Order self;

	...	

	@Override
	public void onEntryHeldForPickup(NoMoreAttempts event) {
       returnToSenderIfNotPickedUp();
	}

	@Override
	public void onEntryHeldForPickup(CouldNotDeliver event) {
	    returnToSenderIfNotPickedUp();
	}
	
	private void returnToSenderIfNotPickedUp() {
		self.signal(new Order.Events.ReturnToSender(),
				Duration.create(14, TimeUnit.DAYS));
	}

	...

}
``` 

### Create the REST API
The project [*xuml-tools/order-tracker-webapp*](order-tracker-webapp) is an example that wraps *order-tracker* with a web-service, in particular a REST API using the [Jersey](https://jersey.java.net/) implementation of [JAX-RS](http://en.wikipedia.org/wiki/Java_API_for_RESTful_Web_Services). 

The guts of the service definition is in [Service.java](order-tracker-webapp/src/main/java/ordertracker/rs/Service.java). Check it out!
