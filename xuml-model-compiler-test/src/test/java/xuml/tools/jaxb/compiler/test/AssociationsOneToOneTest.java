package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import one_to_one.A;
import one_to_one.B;
import one_to_one.B.BId;
import one_to_one.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;

public class AssociationsOneToOneTest {

	private static EntityManagerFactory emf;

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		emf = Persistence.createEntityManagerFactory("one-to-one");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		emf.close();
	}

	@Test
	public void testCreateAWithoutB() {

		A a = new A();
		a.setId(new A.AId("hello", "there"));
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(a);
		em.getTransaction().commit();
		em.close();
	}

	@Test(expected = PersistenceException.class)
	public void testCannotCreateBWithoutA() {
		B b = new B();
		b.setId(new BId("some", "thing"));
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			em.persist(b);
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
			A a2 = em.find(A.class, new A.AId("hello", "there"));
			B b = new B();
			b.setId(new BId("some2", "thing2"));
			b.setA(a2);
			em.persist(b);
			em.getTransaction().commit();
			em.close();
		}
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			A a2 = em.find(A.class, new A.AId("hello", "there"));
			assertNotNull(a2.getB());
			em.getTransaction().commit();
			em.close();
		}
	}

}
