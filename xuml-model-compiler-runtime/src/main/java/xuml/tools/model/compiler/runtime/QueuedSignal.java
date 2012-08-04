package xuml.tools.model.compiler.runtime;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "xuml_queued_signal")
public class QueuedSignal {

	public QueuedSignal() {
		// no-arg constructor required by JPA
	}

	public QueuedSignal(byte[] idContent, String entityClassName,
			String eventClassName, byte[] eventContent, long time,
			Long repeatIntervalMs, String fromEntityUniqueId) {
		this.idContent = idContent;
		this.entityClassName = entityClassName;
		this.eventClassName = eventClassName;
		this.eventContent = eventContent;
		this.fromEntityUniqueId = fromEntityUniqueId;
		this.time = new Date(time);
		this.repeatIntervalMs = repeatIntervalMs;
	}

	// TODO add new fields, numFailures, timeFirstFailure, timeLastFailure

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "signal_id")
	public Long id;

	@Column(name = "entity_class_name", nullable = false)
	public String entityClassName;

	@Column(name = "event_class_name", nullable = false)
	public String eventClassName;

	@Column(name = "id_content", nullable = false)
	public byte[] idContent;

	@Column(name = "event_content", nullable = false)
	public byte[] eventContent;

	@Column(name = "time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date time;

	@Column(name = "repeat_interval_ms", nullable = true)
	public Long repeatIntervalMs;

	@Column(name = "from_entity")
	public String fromEntityUniqueId;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueuedSignal [id=");
		builder.append(id);
		builder.append(", entityClassName=");
		builder.append(entityClassName);
		builder.append(", eventClassName=");
		builder.append(eventClassName);
		builder.append(", idContent=");
		builder.append(Arrays.toString(idContent));
		builder.append(", eventContent=");
		builder.append(Arrays.toString(eventContent));
		builder.append(", time=");
		builder.append(time);
		builder.append(", repeatIntervalMs=");
		builder.append(repeatIntervalMs);
		builder.append(", fromEntityUniqueId=");
		builder.append(fromEntityUniqueId);
		builder.append("]");
		return builder.toString();
	}

}
