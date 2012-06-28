package xuml.tools.jaxb.compiler;

import javax.persistence.Persistence;

import moten.david.util.database.derby.DerbyUtil;

import org.junit.Test;

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
