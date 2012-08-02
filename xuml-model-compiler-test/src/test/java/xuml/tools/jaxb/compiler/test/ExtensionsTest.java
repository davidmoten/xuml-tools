package xuml.tools.jaxb.compiler.test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import extensions.A;
import extensions.Context;

public class ExtensionsTest {

	@BeforeClass
	public static void setup() {
		EntityManagerFactory emf = PersistenceHelper.createEmf("extensions");
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
			// demonstrate method chaining
			A a = new A().setEmail_("fred@somewhere.com").setPostcode_(2601)
					.persist(em);

			// check that field 'one' has been generated
			assertNotNull(a.getId());

			em.getTransaction().commit();

		} finally {
			em.close();
		}
		assertEquals(1, A.findByEmailPostcode("fred@somewhere.com", 2601)
				.size());
		em = Context.createEntityManager();
		assertEquals(1, A.findByEmailPostcode(em, "fred@somewhere.com", 2601)
				.size());
		em.close();
	}

	@Test
	public void testFinder() {
		assertTrue(A.findById(10).isEmpty());
	}

}
