package xuml.tools.model.compiler.runtime.message;

import com.google.common.base.Optional;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.Event;

public class Signal<T> {

    private final Entity<T> entity;
    private final Event<T> event;
    private final long id;
    // epoch time ms to process signal
    private final Long timeMs;
    private final Optional<FiniteDuration> repeatInterval;
    private final String fromEntityUniqueId;

    public Signal(String fromEntityUniqueId, Entity<T> entity, Event<T> event, long id, Long timeMs,
            Optional<FiniteDuration> repeatInterval) {
        this.fromEntityUniqueId = fromEntityUniqueId;
        this.entity = entity;
        this.event = event;
        this.id = id;
        this.timeMs = timeMs;
        this.repeatInterval = repeatInterval;
    }

    public Signal(String fromEntityUniqueId, Entity<T> entity, Event<T> event, long id,
            Long timeMs) {
        this(fromEntityUniqueId, entity, event, id, timeMs, null);
    }

    public Signal(String fromEntityUniqueId, Entity<T> entity, Event<T> event, long id,
            Duration delay, FiniteDuration repeatInterval) {
        this(fromEntityUniqueId, entity, event, id, getTime(delay), Optional.of(repeatInterval));
    }

    private static Long getTime(Duration delay) {
        if (delay == null)
            return null;
        else
            return System.currentTimeMillis() + delay.toMillis();
    }

    public Signal(String fromEntityUniqueId, Entity<T> entity, Event<T> event, long id) {
        this(fromEntityUniqueId, entity, event, id, null);
    }

    public long getId() {
        return id;
    }

    public Optional<FiniteDuration> getRepeatInterval() {
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

    public String getFromEntityUniqueId() {
        return fromEntityUniqueId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Signal [id=");
        builder.append(id);
        builder.append(", event=");
        builder.append(event);
        builder.append(", entity.uniqueId=");
        builder.append(entity.uniqueId());
        builder.append(", timeMs=");
        builder.append(timeMs);
        builder.append(", repeatInterval=");
        builder.append(repeatInterval);
        builder.append("]");
        return builder.toString();
    }

}
