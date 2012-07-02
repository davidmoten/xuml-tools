package xuml.tools.model.compiler.runtime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "xuml_signal")
public class SignalPersistence {

	public SignalPersistence() {
		// jpa requires no-arg constructor
	}

	public SignalPersistence(String idClassName, byte[] idContent,
			String className, byte[] eventContent) {
		this.idClassName = idClassName;
		this.className = className;
		this.idContent = idContent;
		this.eventContent = eventContent;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "signal_id")
	public Long id;

	@Column(name = "id_class_name")
	public String idClassName;

	@Column(name = "class_name", nullable = false)
	public String className;

	@Column(name = "id_content", nullable = false)
	public byte[] idContent;

	@Column(name = "event_content", nullable = false)
	public byte[] eventContent;
}
