package ordertracker.rs;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ordertracker.Context;
import ordertracker.Order;

@Path("/")
public class Service {

	@GET
	@Path("/{param}")
	public Response getMsg(@PathParam("param") String msg) {
		String output = "The parameter is : " + msg;
		return Response.status(200).entity(output).build();
	}

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
		return Response.status(200).entity("order created").build();
	}
	
	@PUT
	@Path("/order/{orderId}/send")
	public Response sendOrder(@PathParam("orderId") String orderId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.Send());
		return Response.status(200).entity("order sent").build();
	}
	
	@PUT
	@Path("/order/{orderId}/assign")
	public Response assignToCourier(@PathParam("orderId") String orderId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.Assign());
		return Response.status(200).entity("order assigned to a courier").build();
	}
	
	@PUT
	@Path("/order/{orderId}/pickedUp")
	public Response pickedUpByCourier(@PathParam("orderId") String orderId) {
		Order order = Order.find(orderId);
		order.signal(new Order.Events.PickedUp());
		return Response.status(200).entity("order picked up by a courier").build();
	}

}