package xuml.tools.model.compiler;

import javax.persistence.Persistence;


import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;

public class EmbeddedIdTest {

	@Test
	public void test() {
		DerbyUtil.disableDerbyLog();
		Persistence.createEntityManagerFactory("embeddedIdTest");
	}

	@Test
	public void dummy() {

	}

}
