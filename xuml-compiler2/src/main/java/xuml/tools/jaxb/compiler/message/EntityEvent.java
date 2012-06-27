package xuml.tools.jaxb.compiler.message;

import xuml.tools.jaxb.compiler.Entity;
import xuml.tools.jaxb.compiler.Event;

public class EntityEvent<T, R> {

	private final Entity<T, R> entity;
	private final Event<T> event;

	public EntityEvent(Entity<T, R> entity, Event<T> event) {
		this.entity = entity;
		this.event = event;

	}

	public Entity<T, R> getEntity() {
		return entity;
	}

	public Event<T> getEvent() {
		return event;
	}
}
