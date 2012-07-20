package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import unary_one.A;
import unary_one.A.AId;
import unary_one.Context;
import xuml.tools.util.database.DerbyUtil;

public class UnaryAssociationToOneTest {
	private static EntityManagerFactory emf;

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		emf = Persistence.createEntityManagerFactory("unary-one");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		emf.close();
	}

	@Test(expected = PersistenceException.class)
	public void testCreateAWithoutParent() {

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
	public void testCreateAWithParentAndIsPersistedProperly() {
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			A a = A.create(new AId("boo", "baa"));
			A parent = A.create(new AId("boo2", "baa2"));
			a.setHasParent(parent);
			a.setHasParentInverse(parent);
			parent.setHasParent(parent);
			parent.setHasParentInverse(parent);
			em.persist(a);
			em.getTransaction().commit();
			em.close();
		}
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			A a = em.find(A.class, new A.AId("boo", "baa"));
			assertNotNull(a.getHasParent());
			em.getTransaction().commit();
			em.close();
		}
	}
}
