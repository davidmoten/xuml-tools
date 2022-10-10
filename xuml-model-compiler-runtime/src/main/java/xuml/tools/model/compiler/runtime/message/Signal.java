package xuml.tools.model.compiler.runtime.message;

import java.io.Serializable;
import java.time.Duration;

import com.google.common.base.Optional;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.Event;

public class Signal<T> {

    private final Class<Entity<T>> entityClass;
    private final Event<T> event;
    private final String id;
    // epoch time ms to process signal
    private final Long timeMs;
    private final Optional<Duration> repeatInterval;
    private final String fromEntityUniqueId;
    private final Serializable entityId;
    private final String entityUniqueId;

    public Signal(String fromEntityUniqueId, Class<Entity<T>> entityClass, Event<T> event,
            String id, Long timeMs, Optional<Duration> repeatInterval, Serializable entityId,
            String entityUniqueId) {
        if (entityId instanceof Optional)
            throw new RuntimeException("unexpected");
        this.fromEntityUniqueId = fromEntityUniqueId;
        this.entityClass = entityClass;
        this.event = event;
        this.id = id;
        this.timeMs = timeMs;
        this.repeatInterval = repeatInterval;
        this.entityId = entityId;
        this.entityUniqueId = entityUniqueId;
    }

    public Signal(String fromEntityUniqueId, Class<Entity<T>> entityClass, Event<T> event,
            String id, Long timeMs, Serializable entityId, String entityUniqueId) {
        this(fromEntityUniqueId, entityClass, event, id, timeMs, null, entityId, entityUniqueId);
    }

    public Signal(String fromEntityUniqueId, Class<Entity<T>> entityClass, Event<T> event,
            String id, Duration delay, Duration repeatInterval, Serializable entityId,
            String entityUniqueId) {
        this(fromEntityUniqueId, entityClass, event, id, getTime(delay),
                Optional.of(repeatInterval), entityId, entityUniqueId);
    }

    private static Long getTime(Duration delay) {
        if (delay == null)
            return null;
        else
            return System.currentTimeMillis() + delay.toMillis();
    }

    public Signal(String fromEntityUniqueId, Class<Entity<T>> entityClass, Event<T> event,
            String id, Serializable entityId, String entityUniqueId) {
        this(fromEntityUniqueId, entityClass, event, id, null, entityId, entityUniqueId);
    }

    public String getId() {
        return id;
    }

    public Optional<Duration> getRepeatInterval() {
        return repeatInterval;
    }

    public Class<Entity<T>> getEntityClass() {
        return entityClass;
    }

    public Event<T> getEvent() {
        return event;
    }

    public Long getTime() {
        return timeMs;
    }

    public Serializable getEntityId() {
        return entityId;
    }

    public String getFromEntityUniqueId() {
        return fromEntityUniqueId;
    }

    public String getEntityUniqueId() {
        return entityUniqueId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Signal [id=");
        builder.append(id);
        builder.append(", event=");
        builder.append(event);
        builder.append(", entityClass");
        builder.append(entityClass.getName());
        builder.append(", timeMs=");
        builder.append(timeMs);
        builder.append(", repeatInterval=");
        builder.append(repeatInterval);
        builder.append("]");
        return builder.toString();
    }

}
