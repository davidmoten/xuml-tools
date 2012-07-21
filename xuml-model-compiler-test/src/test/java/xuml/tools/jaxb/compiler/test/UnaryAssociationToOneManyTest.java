package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import unary_one_many.A;
import unary_one_many.A.AId;
import unary_one_many.Context;
import xuml.tools.model.compiler.runtime.RelationshipNotEstablishedException;
import xuml.tools.util.database.DerbyUtil;

public class UnaryAssociationToOneManyTest {

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("unary-one-many");
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

	@Test(expected = RelationshipNotEstablishedException.class)
	public void testCreateAWithChildrenButChildDoesNotHaveOneChildItselfThrowsException() {

		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			A a = A.create(new AId("boo", "baa"));
			A child = A.create(new AId("boo2", "baa2"));
			a.getHasChildren().add(child);
			child.setHasChildrenInverse(a);
			em.persist(a);
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
			a.setHasChildrenInverse(a);
			child.setHasChildrenInverse(a);
			child.getHasChildren().add(child);
			child.setHasChildrenInverse(child);
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
