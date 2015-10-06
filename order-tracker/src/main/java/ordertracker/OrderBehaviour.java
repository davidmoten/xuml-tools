package ordertracker;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

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
import ordertracker.Order.State;
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
        event(Order.State.PREPARING);
    }

    @Override
    public void onEntryReadyForDispatch(Send event) {
        event(Order.State.READY_FOR_DISPATCH);
    }

    @Override
    public void onEntryCourierAssigned(Assign event) {
        event(Order.State.COURIER_ASSIGNED);
    }

    @Override
    public void onEntryInTransit(PickedUp event) {
        event(Order.State.IN_TRANSIT);
    }

    @Override
    public void onEntryInTransit(ArrivedDepot event) {
        Optional<Depot> depot = Depot.find(event.getDepotID());
        if (!depot.isPresent())
            throw new RuntimeException("depot does not exist: " + event.getDepotID());
        else {
            self.setDepot_R1(depot.get());
            event(Order.State.IN_TRANSIT);
        }
    }

    @Override
    public void onEntryReadyForDelivery(ArrivedFinalDepot event) {
        Optional<Depot> depot = Depot.find(event.getDepotID());
        if (!depot.isPresent())
            throw new RuntimeException("depot does not exist: " + event.getDepotID());
        else {
            self.setDepot_R1(depot.get());
            event(Order.State.READY_FOR_DELIVERY);
        }
    }

    @Override
    public void onEntryDelivering(Delivering event) {
        self.setAttempts(self.getAttempts() + 1);
        event(Order.State.DELIVERING);
    }

    @Override
    public void onEntryDelivered(Delivered event) {
        // send email to destination email and sender email notifying of
        // successful delivery
        event(Order.State.DELIVERED);
    }

    @Override
    public void onEntryDeliveryFailed(DeliveryFailed event) {
        event(Order.State.DELIVERY_FAILED);
        if (self.getAttempts() >= self.getMaxAttempts())
            self.signal(new Order.Events.NoMoreAttempts());
        else
            self.signal(new Order.Events.DeliverAgain());
    }

    @Override
    public void onEntryAwaitingNextDeliveryAttempt(DeliverAgain event) {
        self.signal(new Order.Events.DeliverAgain(), Duration.create(12, TimeUnit.HOURS));
        event(Order.State.AWAITING_NEXT_DELIVERY_ATTEMPT);
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
        self.signal(new Order.Events.ReturnToSender(), Duration.create(14, TimeUnit.DAYS));
        event(Order.State.HELD_FOR_PICKUP);
    }

    @Override
    public void onEntryReturnToSender(ReturnToSender event) {
        // at this point we might create another order for the return leg
        event(Order.State.RETURN_TO_SENDER);
    }

    @Override
    public void onEntryDelivered(DeliveredByPickup event) {
        event(Order.State.DELIVERED);
    }

    private void event(State state) {
        // send the state to the singleton event entity
        SystemEvent.find("1").get().signal(new SystemEvent.Events.NewEvent(state.toString()));
    }

    @Override
    public void onEntryReadyForDelivery(DeliverAgain event) {
        event(Order.State.READY_FOR_DELIVERY);
    }

}
