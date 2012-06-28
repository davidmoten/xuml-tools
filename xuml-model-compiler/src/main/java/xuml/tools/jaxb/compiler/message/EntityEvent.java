package xuml.tools.jaxb.compiler.message;

import xuml.tools.jaxb.compiler.Entity;
import xuml.tools.jaxb.compiler.Event;

public class EntityEvent<T> implements HasEntity<T> {

	private final Entity<T> entity;
	private final Event<T> event;

	public EntityEvent(Entity<T> entity, Event<T> event) {
		this.entity = entity;
		this.event = event;
	}

	@Override
	public Entity<T> getEntity() {
		return entity;
	}

	public Event<T> getEvent() {
		return event;
	}
}
