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

import org.junit.Assert;
import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;

public class AssociationsOneToOneTest {

	@Test
	public void testZeroOneToOne() {
		DerbyUtil.disableDerbyLog();
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("one-to-one");
		Context.setEntityManagerFactory(emf);
		A a = new A();
		a.setId(new A.AId("hello", "there"));
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			em.persist(a);
			em.getTransaction().commit();
			em.close();
		}
		{
			B b = new B();
			b.setId(new BId("some", "thing"));
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			try {
				em.persist(b);
				Assert.fail();
			} catch (PersistenceException e) {
				em.getTransaction().rollback();
			}
			em.close();
		}
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
		emf.close();
	}
}
