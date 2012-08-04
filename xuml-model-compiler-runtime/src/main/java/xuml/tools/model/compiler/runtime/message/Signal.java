package xuml.tools.model.compiler.runtime.message;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.Event;
import akka.util.Duration;

public class Signal<T> {

	private final Entity<T> entity;
	private final Event<T> event;
	private final long id;
	// epoch time ms to process signal
	private final Long timeMs;
	private final Duration repeatInterval;

	public Signal(Entity<T> entity, Event<T> event, long id, Long timeMs,
			Duration repeatInterval) {
		this.entity = entity;
		this.event = event;
		this.id = id;
		this.timeMs = timeMs;
		this.repeatInterval = repeatInterval;
	}

	public Signal(Entity<T> entity, Event<T> event, long id, Long timeMs) {
		this(entity, event, id, timeMs, null);
	}

	public Signal(Entity<T> entity, Event<T> event, long id, Duration delay,
			Duration repeatInterval) {
		this(entity, event, id, getTime(delay), repeatInterval);
	}

	private static Long getTime(Duration delay) {
		if (delay == null)
			return null;
		else
			return System.currentTimeMillis() + delay.toMillis();
	}

	public Signal(Entity<T> entity, Event<T> event, long id) {
		this(entity, event, id, null);
	}

	public long getId() {
		return id;
	}

	public Duration getRepeatInterval() {
		return repeatInterval;
	}

	public Entity<T> getEntity() {
		return entity;
	}

	public Event<T> getEvent() {
		return event;
	}

	public Long getTime() {
		return timeMs;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Signal [entity.uniqueId=");
		builder.append(entity.uniqueId());
		builder.append(", event=");
		builder.append(event);
		builder.append(", id=");
		builder.append(id);
		builder.append(", timeMs=");
		builder.append(timeMs);
		builder.append(", repeatInterval=");
		builder.append(repeatInterval);
		builder.append("]");
		return builder.toString();
	}

}
