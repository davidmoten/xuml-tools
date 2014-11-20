package ordertracker;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import ordertracker.Order.Behaviour;
import ordertracker.Order.Events.ArrivedDepot;
import ordertracker.Order.Events.ArrivedFinalDepot;
import ordertracker.Order.Events.Assign;
import ordertracker.Order.Events.CouldNotDeliver;
import ordertracker.Order.Events.Create;
import ordertracker.Order.Events.DeliverAgain;
import ordertracker.Order.Events.Delivered;
import ordertracker.Order.Events.DeliveredByPickup;
import ordertracker.Order.Events.Delivering;
import ordertracker.Order.Events.DeliveryFailed;
import ordertracker.Order.Events.NoMoreAttempts;
import ordertracker.Order.Events.PickedUp;
import ordertracker.Order.Events.ReturnToSender;
import ordertracker.Order.Events.Send;
import scala.concurrent.duration.Duration;

public class OrderBehaviour implements Order.Behaviour {

	private static Logger log = Logger.getLogger(OrderBehaviour.class);

	private Order self;

	private OrderBehaviour(Order self) {
		this.self = self;
	}

	@Override
	public void onEntryPreparing(Create event) {
		self.setAttempts(0);
		self.setComment(event.getComment());
		self.setDescription(event.getDescription());
		self.setDestinationEmail(event.getDestinationEmail());
		self.setFromAddress(event.getFromAddress());
		self.setId(event.getOrderID());
		self.setMaxAttempts(event.getMaxAttempts());
		self.setSenderEmail(event.getSenderEmail());
		self.setToAddress(event.getToAddress());
	}

	@Override
	public void onEntryReadyForDispatch(Send event) {
		// do nothing
	}

	@Override
	public void onEntryCourierAssigned(Assign event) {
		// do nothing
	}

	@Override
	public void onEntryInTransit(PickedUp event) {
		// do nothing
	}

	@Override
	public void onEntryInTransit(ArrivedDepot event) {
		Depot depot = Depot.find(event.getDepotID());
		self.setDepot_R1(depot);
	}

	@Override
	public void onEntryReadyForDelivery(ArrivedFinalDepot event) {
		Depot depot = Depot.find(event.getDepotID());
		self.setDepot_R1(depot);
	}

	@Override
	public void onEntryDelivering(Delivering event) {
		self.setAttempts(self.getAttempts() + 1);
	}

	@Override
	public void onEntryDelivered(Delivered event) {
		// send email to destination email and sender email notifying of
		// successful delivery
	}

	@Override
	public void onEntryHeldForPickup(DeliveryFailed event) {
		// return to sender after 14 days if customer does not pickup
		self.signal(new Order.Events.ReturnToSender(),
				Duration.create(14, TimeUnit.DAYS));
	}

	@Override
	public void onEntryDeliveryFailed(DeliveryFailed event) {
		if (self.getAttempts() >= self.getMaxAttempts())
			self.signal(new Order.Events.NoMoreAttempts());
		else
			self.signal(new Order.Events.DeliverAgain(),
					Duration.create(12, TimeUnit.HOURS));
	}

	@Override
	public void onEntryReadyForDelivery(DeliverAgain event) {
		//do nothing
	}

	@Override
	public void onEntryHeldForPickup(NoMoreAttempts event) {
		//do nothing
	}

	@Override
	public void onEntryHeldForPickup(CouldNotDeliver event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntryReturnToSender(ReturnToSender event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntryDelivered(DeliveredByPickup event) {
		// TODO Auto-generated method stub

	}

	static Order.BehaviourFactory createFactory() {
		return new Order.BehaviourFactory() {

			@Override
			public Behaviour create(Order entity) {
				return new OrderBehaviour(entity);
			}
		};
	}

}
