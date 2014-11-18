Implementing an Order Tracker using xuml-tools
===============================================

The increasing popularity of microservices is a perfect opportunity to make the most of Executable UML with discrete systems with their own RDB persistence layer and perhaps a service offering e.g. REST to allow a bridge between a subsystems and others.

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

**Order** - orderId, description, fromAddress, toAddress, destinationEmail, senderEmail, lastDepotId, maxAttempts, comment

**Depot** - depotId, name, lat, long

The states for *Order* are:

* *Preparing*
* *Ready For dispatch*
* *In transit*
* *Ready For delivery*
* *Delivering*
* *Delivered**
* *Held for pickup*
* *Could not deliver*
* *Return to sender*

The transitions are:

* *Preparing* -> *Ready for dispatch* : *send*
* *Ready for dispatch* -> *In transit* :*picked up*
* *In transit* -> *Ready for delivery* : *at final depot*
* *Ready for delivery* -> *Delivering* : *delivering*



