package ordertracker;

import java.util.concurrent.TimeUnit;

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

	private final Order self;

	public OrderBehaviour(Order self) {
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
	}

	@Override
	public void onEntryCourierAssigned(Assign event) {
	}

	@Override
	public void onEntryInTransit(PickedUp event) {
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
	public void onEntryDeliveryFailed(DeliveryFailed event) {
		if (self.getAttempts() >= self.getMaxAttempts())
			self.signal(new Order.Events.NoMoreAttempts());
		else
			self.signal(new Order.Events.DeliverAgain());
	}

	@Override
	public void onEntryAwaitingNextDeliveryAttempt(DeliverAgain event) {
		self.signal(new Order.Events.DeliverAgain(),
				Duration.create(12, TimeUnit.SECONDS));
	}

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

	@Override
	public void onEntryReturnToSender(ReturnToSender event) {
		// at this point we might create another order for the return leg
	}

	@Override
	public void onEntryDelivered(DeliveredByPickup event) {
	}

}
