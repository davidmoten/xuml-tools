package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.model.compiler.runtime.RelationshipNotEstablished;
import xuml.tools.util.database.DerbyUtil;
import zero_one_to_one_many.A;
import zero_one_to_one_many.A.AId;
import zero_one_to_one_many.B;
import zero_one_to_one_many.B.BId;
import zero_one_to_one_many.Context;

public class BinaryAssociationZeroOneToOneManyTest {

	private static EntityManagerFactory emf;

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		emf = Persistence.createEntityManagerFactory("zero-one-to-one-many");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		emf.close();
	}

	@Test(expected = RelationshipNotEstablished.class)
	public void testCreateAWithoutB() {

		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			A.create(new A.AId("hello", "there")).persist(em);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Test
	public void testCanCreateBWithoutA() {

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		B.create(new BId("some", "thing")).persist(em);
		em.getTransaction().commit();
		em.close();
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
