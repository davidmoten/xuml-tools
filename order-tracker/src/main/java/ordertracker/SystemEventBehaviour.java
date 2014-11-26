package ordertracker;

import ordertracker.SystemEvent.Events.Create;
import ordertracker.SystemEvent.Events.NewEvent;

public class SystemEventBehaviour implements SystemEvent.Behaviour{

	private SystemEvent self;

	public SystemEventBehaviour(SystemEvent self) {
		this.self = self;
	}
	
	@Override
	public void onEntryCreated(Create event) {
		self.setId(event.getEventID());
	}

	@Override
	public void onEntryCreated(NewEvent event) {
		//call event service (a bridge)
		EventService.instance().event(event.getData());
	}

}
