package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;
import extensions.A;
import extensions.Context;

public class ExtensionsTest {

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("extensions");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		Context.close();
	}

	@Test
	public void testTypesAreGeneratedAsExpectedForEachMember() {
		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			A a = new A();

			a.persist(em);

			// check that field 'one' has been generated
			assertNotNull(a.getId());

			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

}
