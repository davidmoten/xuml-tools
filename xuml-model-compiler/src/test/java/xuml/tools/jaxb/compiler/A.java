package xuml.tools.jaxb.compiler;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "TableA")
public class A implements Serializable {

	@EmbeddedId
	private AId id;

	@Embeddable
	public static class AId implements Serializable {
		@Column(name = "a_one")
		private String a1;

		@Column(name = "a_two")
		private String a2;

	}

	@Column(name = "a_three")
	private String a3;

}
