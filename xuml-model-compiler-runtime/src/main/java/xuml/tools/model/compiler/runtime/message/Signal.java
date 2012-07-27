package xuml.tools.model.compiler.runtime.message;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.Event;

public class Signal<T> {

	private final Entity<T> entity;
	private final Event<T> event;
	private final long id;

	public Signal(Entity<T> entity, Event<T> event, long id) {
		this.entity = entity;
		this.event = event;
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public Entity<T> getEntity() {
		return entity;
	}

	public Event<T> getEvent() {
		return event;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Signal [entity=");
		builder.append(entity.uniqueId());
		builder.append(", event=");
		builder.append(event);
		builder.append(", id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}

}
