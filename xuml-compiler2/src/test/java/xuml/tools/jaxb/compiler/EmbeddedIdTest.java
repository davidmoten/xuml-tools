package xuml.tools.jaxb.compiler;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

public class EmbeddedIdTest {

	@Test
	// TODO get this working
	public void test() {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("embeddedIdTest");
	}

	@Test
	public void dummy() {

	}

}
