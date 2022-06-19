package xuml.tools.model.compiler.runtime;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

@Entity
@Table(name = "xuml_queued_signal")
public class QueuedSignal {

    public QueuedSignal() {
        // no-arg constructor required by JPA
    }

    public QueuedSignal(String idClassName, byte[] idContent, String entityClassName,
            String eventClassName, byte[] eventContent, long time, Optional<Long> repeatIntervalMs,
            String fromEntityUniqueId, String toEntityUniqueId) {
        this.toEntityUniqueId = toEntityUniqueId;
        Preconditions.checkNotNull(repeatIntervalMs);
        this.idContent = idContent;
        this.idClassName = idClassName;
        this.entityClassName = entityClassName;
        this.eventClassName = eventClassName;
        this.eventContent = eventContent;
        this.fromEntityUniqueId = fromEntityUniqueId;
        this.time = new Date(time);
        this.repeatIntervalMs = repeatIntervalMs.orNull();
        this.id = ArbitraryId.next();
    }

    // TODO add new fields, numFailures, timeFirstFailure, timeLastFailure

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "signal_id")
    public String id;

    @Column(name = "entity_class_name", nullable = false)
    public String entityClassName;

    @Column(name = "event_class_name", nullable = false)
    public String eventClassName;

    @Column(name = "id_class_name", nullable = false)
    public String idClassName;

    @Column(name = "id_content", nullable = false)
    public byte[] idContent;

    @Column(name = "event_content", nullable = false)
    @Lob
    public byte[] eventContent;

    @Column(name = "time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date time;

    @Column(name = "repeat_interval_ms", nullable = true)
    public Long repeatIntervalMs;

    @Column(name = "from_entity_unique_id")
    public String fromEntityUniqueId;

    @Column(name = "to_entity_unique_id", nullable = false)
    public String toEntityUniqueId;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QueuedSignal [id=");
        builder.append(id);
        builder.append(", entityClassName=");
        builder.append(entityClassName);
        builder.append(", eventClassName=");
        builder.append(eventClassName);
        builder.append(", idClassName=");
        builder.append(idClassName);
        builder.append(", idContentSize=");
        builder.append(idContent.length);
        builder.append(", eventContentSize=");
        builder.append(eventContent.length);
        builder.append(", time=");
        builder.append(time);
        builder.append(", repeatIntervalMs=");
        builder.append(repeatIntervalMs);
        builder.append(", fromEntityUniqueId=");
        builder.append(fromEntityUniqueId);
        builder.append("]");
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    public Class<Event<?>> eventClass() {
        try {
            return (Class<Event<?>>) Class.forName(eventClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Serializable> idClass() {
        try {
            return (Class<? extends Serializable>) Class.forName(idClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
