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

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "signal_id")
	public Long id;

	@Column(name = "signal_uuid", unique = true, nullable = false)
	public String uuid;

	@Column(name = "name", nullable = false)
	public String name;

	@Column(name = "signal", nullable = false)
	public byte[] signal;
}
