package xuml.tools.model.compiler;

import javax.persistence.Persistence;


import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;

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
