package xuml.tools.jaxb.compiler.test;

import static javax.persistence.Persistence.createEntityManagerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import specialization.Context;
import xuml.tools.util.database.DerbyUtil;

public class SpecializationTest {

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		Context.setEntityManagerFactory(createEntityManagerFactory("specialization"));
	}

	@AfterClass
	public static void close() {
		Context.stop();
		Context.close();
	}

	@Test
	public void dummy() {
		// TOOD
	}
}
