package xuml.tools.jaxb.compiler.test;

import javax.persistence.Persistence;

import moten.david.util.database.derby.DerbyUtil;

import org.junit.Test;

import abc.A;

public class AbcTest {

	@Test
	public void test() {
		DerbyUtil.disableDerbyLog();
		Persistence.createEntityManagerFactory("abc");

	}

	@Test(expected = NullPointerException.class)
	public void testBehaviourNotSetForAThrowsException() {
		new A();
	}
}
