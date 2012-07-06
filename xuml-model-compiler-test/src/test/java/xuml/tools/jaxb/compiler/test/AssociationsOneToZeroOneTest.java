package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import one_to_zero_one.A;
import one_to_zero_one.A.AId;
import one_to_zero_one.B;
import one_to_zero_one.B.BId;
import one_to_zero_one.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;

public class AssociationsOneToZeroOneTest {

	private static EntityManagerFactory emf;

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		emf = Persistence.createEntityManagerFactory("one-to-zero-one");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		emf.close();
	}

	@Test
	public void testCreateAWithoutB() {

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		A a = A.create(new A.AId("hello", "there")).persist(em);
		em.getTransaction().commit();
		em.close();
	}

	@Test(expected = PersistenceException.class)
	public void testCannotCreateBWithoutA() {

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			B b = B.create(new BId("some", "thing")).persist(em);
		} finally {
			em.getTransaction().rollback();
			em.close();
		}
	}

	@Test
	public void testCreateAWithBAndIsPersistedProperly() {
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			A a2 = A.create(new AId("boo", "baa"));
			B b = B.create(new BId("some2", "thing2"));
			b.setA(a2);
			em.persist(b);
			em.getTransaction().commit();
			em.close();
		}
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			A a2 = em.find(A.class, new A.AId("boo", "baa"));
			assertNotNull(a2.getB());
			em.getTransaction().commit();
			em.close();
		}
	}

}