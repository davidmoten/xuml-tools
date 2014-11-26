package ordertracker.rs;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ordertracker.Context;
import ordertracker.Depot;
import ordertracker.Order;

@Path("/")
public class Service {

	@POST
	@Path("/order/{orderId}/create")
	public Response createOrder(@PathParam("orderId") String orderId,
			@QueryParam("description") String description,
			@QueryParam("fromAddress") String fromAddress,
			@QueryParam("toAddress") String toAddress,
			@QueryParam("destinationEmail") String destinationEmail,
			@QueryParam("senderEmail") String senderEmail,
			@QueryParam("maxAttempts") Integer maxAttempts,
			@QueryParam("comment") String comment) {
		Context.create(Order.class, new Order.Events.Create(orderId,
				description, fromAddress, toAddress, destinationEmail,
				senderEmail, maxAttempts, comment));
		return Response.ok("order created").build();
	}

	@PUT
	@Path("/order/{orderId}/send")
	public Response sendOrder(@PathParam("orderId") String orderId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.Send());
		return Response.ok("order sent").build();
	}

	@PUT
	@Path("/order/{orderId}/assign")
	public Response assignToCourier(@PathParam("orderId") String orderId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.Assign());
		return Response.ok("order assigned to a courier").build();
	}

	@PUT
	@Path("/order/{orderId}/pickedUp")
	public Response pickedUpByCourier(@PathParam("orderId") String orderId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.PickedUp());
		return Response.ok("order picked up by a courier").build();
	}

	@PUT
	@Path("/order/{orderId}/arrivedDepot")
	public Response arrivedDepot(@PathParam("orderId") String orderId,
			@QueryParam("depotId") String depotId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.ArrivedDepot(depotId));
		return Response.ok("arrived depot " + depotId).build();
	}

	@PUT
	@Path("/order/{orderId}/arrivedFinalDepot")
	public Response arrivedFinalDepot(@PathParam("orderId") String orderId,
			@QueryParam("depotId") String depotId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.ArrivedFinalDepot(depotId));
		return Response.ok("arrived final depot " + depotId).build();
	}

	@PUT
	@Path("/order/{orderId}/delivering")
	public Response delivering(@PathParam("orderId") String orderId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.Delivering());
		return Response.ok("delivering").build();
	}

	@PUT
	@Path("/order/{orderId}/delivered")
	public Response delivered(@PathParam("orderId") String orderId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.Delivered());
		return Response.ok("delivered").build();
	}

	@PUT
	@Path("/order/{orderId}/deliveryFailed")
	public Response deliveryFailed(@PathParam("orderId") String orderId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.DeliveryFailed());
		return Response.ok("delivery failed").build();
	}

	@PUT
	@Path("/order/{orderId}/deliveryAgain")
	public Response deliverAgain(@PathParam("orderId") String orderId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.DeliverAgain());
		return Response.ok("marked for delivery again").build();
	}

	@GET
	@Path("/depot/{depotId}/ordersReadyForDelivery")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrdersReadyForDelivery(
			@PathParam("depotId") String depotId) {
		Depot depot = Depot.find(depotId);
		List<Order> list = new ArrayList<Order>();
		for (Order order : depot.getOrder_R1())
			if (Order.State.READY_FOR_DELIVERY.toString().equals(
					order.getStatus()))
				list.add(order);

		return Response.ok("{ \"count\" : \"" + list.size() + "\"}",
				MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/order/{orderId}/status")
	@Produces("text/plain")
	public Response getOrderStatus(@PathParam("orderId") String orderId) {
		Order order = Order.find(orderId);
		return Response.ok(order.getState(), MediaType.TEXT_PLAIN).build();
	}

}