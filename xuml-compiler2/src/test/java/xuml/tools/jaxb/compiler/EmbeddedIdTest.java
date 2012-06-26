package xuml.tools.jaxb.compiler;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import moten.david.util.database.derby.DerbyUtil;

import org.junit.Test;

public class EmbeddedIdTest {

	@Test
	// TODO get this working
	public void test() {
		DerbyUtil.disableDerbyLog();
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("embeddedIdTest");
	}

	@Test
	public void dummy() {

	}

}
