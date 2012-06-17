package xuml.tools.jaxb.compiler;

import javax.persistence.Persistence;

import org.junit.Test;

public class DatabaseCreationTest {

	// @Test
	public void testCreate() {
		Persistence.createEntityManagerFactory("db-derby");
	}

	@Test
	public void dummy() {

	}

}
