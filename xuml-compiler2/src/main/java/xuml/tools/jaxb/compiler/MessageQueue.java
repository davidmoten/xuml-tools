package xuml.tools.jaxb.compiler;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(schema = "mq", name = "message_queue")
public class MessageQueue {

	@Id
	@GeneratedValue
	private Long id;

}
