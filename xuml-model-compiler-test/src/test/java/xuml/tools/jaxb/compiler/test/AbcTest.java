package xuml.tools.jaxb.compiler.test;

import javax.persistence.Persistence;

import moten.david.util.database.derby.DerbyUtil;

import org.junit.Test;

public class AbcTest {

	@Test
	public void test() {
		DerbyUtil.disableDerbyLog();
		Persistence.createEntityManagerFactory("abc");
	}

}
