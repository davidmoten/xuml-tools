package ordertracker;

import ordertracker.Depot.Events.Create;

public class DepotBehaviour implements Depot.Behaviour {

    private final Depot self;

    public DepotBehaviour(Depot self) {
        this.self = self;
    }

    @Override
    public void onEntryCreated(Create event) {
        // use method chaining
        self.setId_(event.getDepotID()).setName_(event.getName()).setLatitude_(event.getLatitudet())
                .setLongitude_(event.getLongitude());
    }

}
