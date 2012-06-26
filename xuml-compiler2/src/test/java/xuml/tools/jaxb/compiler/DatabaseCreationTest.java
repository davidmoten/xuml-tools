package xuml.tools.jaxb.compiler;

import javax.persistence.Persistence;

import moten.david.util.database.derby.DerbyUtil;

import org.junit.Test;

public class DatabaseCreationTest {

	// @Test
	public void testCreate() {
		DerbyUtil.disableDerbyLog();
		Persistence.createEntityManagerFactory("db-derby");
	}

	@Test
	public void dummy() {

	}

}
