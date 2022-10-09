package ordertracker;

import ordertracker.SystemEvent.Events.Create;
import ordertracker.SystemEvent.Events.NewEvent;

public class SystemEventBehaviour implements SystemEvent.Behaviour {

    private SystemEvent self;

    public SystemEventBehaviour(SystemEvent self) {
        this.self = self;
    }

    @Override
    public void onEntryCreated(Create event) {
        System.out.println("Create");
        self.setId(event.getEventID());
    }

    @Override
    public void onEntryHasEvent(NewEvent event) {
        System.out.println("NewEvent");
        // call event service (a bridge)
        EventService.instance().event(event.getData());        
    }

}
