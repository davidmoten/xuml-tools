Implementing an Order Tracker using xuml-tools
===============================================

The increasing popularity of microservices is a perfect opportunity to make the most of Executable UML with discrete systems with their own relational database for persistence and perhaps a service offering e.g. REST to allow a bridge between a subsystems and others.

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

**Order** - orderId, description, fromAddress, toAddress, destinationEmail, senderEmail, lastDepotId, nextDepotId, maxAttempts, attempts, comment

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

* *Preparing* -> *Ready for dispatch* : *send*
* *Ready for dispatch* -> *Courier assigned* : *assign*
* *Courier assigned* -> *In transit* :*picked up*
* *In transit* -> *In transit* : *transit leg*
* *In transit* -> *Ready for delivery* : *at final depot*
* *Ready for delivery* -> *Delivering* : *delivering*
* *Delivering* -> *Delivered* : *delivered*
* *Delivering* -> *Awaiting next delivery attempt* : *deliveryFailed* 
* *Awaiting next delivery attempt* -> *Ready for delivery* : *deliverAgain* (delay till next day)
* *Ready for delivery* -> *Held for pickup* : *no more attempts*
* *Held for pickup* -> *Delivered* : *delivered*
* *Delivering* -> *Could not deliver* : *could not deliver*
* *Held for pickup* -> *Return to sender* : *returnToSender* (delay 14 days)

API interactions
------------------
The following interactions might occur with the API.

* An *Ordering system* would interact with the *Order Tracker* by creating an *Order* and then signalling *Order* instances with events using the *Order Traffic* Rest API.
* The *Ordering system* might schedule deliveries from each depot each day by asking the *Order Tracker* system for all orders in state *Ready for delivery* at the depot.
* The *Ordering system* might also schedule pickups from senders by requesting all orders in state *Ready for dispatch* to then assign to a courier.
* A recipient might want to know the current location of an order.

Where do we start?
-------------------
Create a project using the archetype:


The first step is to transfer what we know about the classes, attributes, relationships, states and transitions of the Order Tracker subsystem to the *domains.xml* based on the miUML schema.



