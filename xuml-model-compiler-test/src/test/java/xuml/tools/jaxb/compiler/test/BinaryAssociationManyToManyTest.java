package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import one_to_many.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;

public class BinaryAssociationManyToManyTest {
	private static EntityManagerFactory emf;

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		emf = Persistence.createEntityManagerFactory("many-to-many");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		emf.close();
	}

	@Test
	public void test() {
		// TODO
	}

}
