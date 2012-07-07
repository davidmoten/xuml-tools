package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import secondary_identifiers.A;
import secondary_identifiers.Context;
import xuml.tools.util.database.DerbyUtil;

public class SecondaryIdentifiersTest {
	private static EntityManagerFactory emf;

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		emf = Persistence.createEntityManagerFactory("secondary-identifiers");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		emf.close();
	}

	@Test(expected = PersistenceException.class)
	public void testCreateAOtherRequiredIds() {

		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			A.create("boo").persist(em);
			// any participant in an identifier should not be nullable so must
			// set them. Not setting them should throw an exception.
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

}
