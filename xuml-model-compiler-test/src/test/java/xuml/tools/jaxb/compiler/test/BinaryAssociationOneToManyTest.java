package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import one_to_many.A;
import one_to_many.A.AId;
import one_to_many.B;
import one_to_many.B.BId;
import one_to_many.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;

public class BinaryAssociationOneToManyTest {

	private static EntityManagerFactory emf;

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		emf = Persistence.createEntityManagerFactory("one-to-many");
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
		A.create(new A.AId("hello", "there")).persist(em);
		em.getTransaction().commit();
		em.close();
	}

	@Test(expected = PersistenceException.class)
	public void testCannotCreateBWithoutA() {

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			B.create(new BId("some", "thing")).persist(em);
		} finally {
			em.getTransaction().rollback();
			em.close();
		}
	}

	@Test
	public void testCreateAWithMultipleBAndIsPersistedProperly() {
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			A a = A.create(new AId("boo", "baa"));
			B b = B.create(new BId("some2", "thing2"));
			B b2 = B.create(new BId("some3", "thing3"));
			a.getB().add(b);
			a.getB().add(b2);
			b.setA(a);
			b2.setA(a);
			a.persist(em);
			b.persist(em);
			b2.persist(em);
			em.getTransaction().commit();
			em.close();
		}
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			A a2 = em.find(A.class, new A.AId("boo", "baa"));
			assertEquals(2, a2.getB().size());
			em.getTransaction().commit();
			em.close();
		}
	}

}