package ordertracker;

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

public class OrderBehaviour implements Order.Behaviour {

	private Order self;

	private OrderBehaviour(Order self) {
		this.self = self;
	}
	
	@Override
	public void onEntryPreparing(Create event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryReadyForDispatch(Send event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryCourierAssigned(Assign event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryInTransit(PickedUp event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryInTransit(ArrivedDepot event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryReadyForDelivery(ArrivedFinalDepot event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryDelivering(Delivering event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryDelivered(Delivered event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryHeldForPickup(DeliveryFailed event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryReadyForDelivery(DeliverAgain event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryHeldForPickup(NoMoreAttempts event) {
		// TODO Auto-generated method stub
		
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
