package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import unary_many.A;
import unary_many.A.AId;
import unary_many.Context;
import xuml.tools.util.database.DerbyUtil;

public class UnaryAssociationToManyTest {

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		EntityManagerFactory emf = PersistenceHelper.createEmf("unary-many");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		Context.close();
	}

	public void testCreateAWithoutChildren() {

		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			A.create(new A.AId("hello", "there")).persist(em);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Test
	public void testCreateAWithChildrenAndIsPersistedProperly() {
		{
			EntityManager em = Context.createEntityManager();
			em.getTransaction().begin();
			A a = A.create(new AId("boo", "baa"));
			A child = A.create(new AId("boo2", "baa2"));
			a.getHasChildren().add(child);
			child.setHasChildrenInverse(a);
			em.persist(a);
			em.getTransaction().commit();
			em.close();
		}
		{
			EntityManager em = Context.createEntityManager();
			em.getTransaction().begin();
			A a = em.find(A.class, new A.AId("boo", "baa"));
			assertEquals(1, a.getHasChildren().size());
			em.getTransaction().commit();
			em.close();
		}
	}
}
