package ordertracker.rs;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

	@PUT
	@Path("/order/{orderId}/create")
	public Response createOrder(@PathParam("orderId") String orderId,
			@FormParam("description") String description,
			@FormParam("fromAddress") String fromAddress,
			@FormParam("toAddress") String toAddress,
			@FormParam("destinationEmail") String destinationEmail,
			@FormParam("senderEmail") String senderEmail,
			@FormParam("maxAttempts") Integer maxAttempts,
			@FormParam("comment") String comment) {
		Context.create(Order.class, new Order.Events.Create(orderId,
				description, fromAddress, toAddress, destinationEmail,
				senderEmail, maxAttempts, comment));
		return Response.status(200).entity("order created").build();
	}

}