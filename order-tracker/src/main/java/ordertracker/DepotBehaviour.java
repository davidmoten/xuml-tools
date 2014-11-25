package ordertracker;

import ordertracker.Depot.Events.Create;

public class DepotBehaviour implements Depot.Behaviour {

	private final Depot self;

	public DepotBehaviour(Depot self) {
		this.self = self;
	}

	@Override
	public void onEntryCreated(Create event) {
		self.setId(event.getDepotID());
		self.setName(event.getName());
		self.setLatitude(event.getLatitudet());
		self.setLongitude(event.getLongitude());
	}

}
