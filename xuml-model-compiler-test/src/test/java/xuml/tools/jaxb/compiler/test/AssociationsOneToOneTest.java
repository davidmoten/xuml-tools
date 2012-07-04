package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

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
		B b = new B();
		b.setId(new BId("some", "thing"));
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			em.persist(b);
			try {
				em.getTransaction().commit();
				Assert.fail();
			} catch (RollbackException e) {
			}
			em.close();
		}
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			em.merge(b);
			em.merge(a);
			a.setB(b);
			b.setA(a);
			em.getTransaction().commit();
			em.close();
		}
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			em.merge(b);
			assertNotNull(b.getA());
			em.getTransaction().commit();
			em.close();
		}
		emf.close();
	}
}
