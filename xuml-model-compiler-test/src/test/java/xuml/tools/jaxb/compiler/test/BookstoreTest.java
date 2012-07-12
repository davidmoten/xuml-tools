package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;
import bookstore.Context;

public class BookstoreTest {

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();

		// create the entity manager factory
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("bookstore");

		// pass the EntityManagerFactory to the generated xuml Context
		Context.setEntityManagerFactory(emf);

	}

	@AfterClass
	public static void cleanup() {

		// shutdown the actor system
		Context.stop();

		// close the entity manager factory if desired
		Context.close();
	}

	@Test
	public void testCreateEntityManager() {
		EntityManager em = Context.createEntityManager();
		em.close();
	}
}
