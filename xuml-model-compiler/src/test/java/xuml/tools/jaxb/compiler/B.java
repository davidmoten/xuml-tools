package xuml.tools.jaxb.compiler;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "TableB")
public class B implements Serializable {

	@EmbeddedId
	private BId id;

	// @MapsId
	@ManyToOne
	@JoinColumns(value = {
			@JoinColumn(name = "b_a_one", referencedColumnName = "a_one", insertable = false, updatable = false),
			@JoinColumn(name = "b_a_two", referencedColumnName = "a_two", insertable = false, updatable = false) })
	private A a;

	@Embeddable
	public static class BId implements Serializable {

		@Column(name = "b_a_one", insertable = false, updatable = false)
		private String a1;

		@Column(name = "b_a_two", insertable = false, updatable = false)
		private String a2;

		@Column(name = "b_one")
		private String b1;

	}

}
