package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import one_many_to_one_many_association.A;
import one_many_to_one_many_association.B;
import one_many_to_one_many_association.C;
import one_many_to_one_many_association.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.model.compiler.runtime.RelationshipNotEstablishedException;

public class BinaryAssociationOneManyToOneManyAssociationClassTest {

	@BeforeClass
	public static void setup() {
		EntityManagerFactory emf = PersistenceHelper
				.createEmf("one-many-to-one-many-association");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		Context.close();
	}

	// TODO many more tests can be added here

	@Test(expected = RelationshipNotEstablishedException.class)
	public void testCannotCreateBWithoutInstanceOfAViaC() {

		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			B.create("b").persist(em);
		} finally {
			em.close();
		}
	}

	@Test(expected = RelationshipNotEstablishedException.class)
	public void testCanCreateInstanceOfAWithoutB() {

		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			A.create("a").persist(em);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Test(expected = RelationshipNotEstablishedException.class)
	public void testCanCreateInstanceOfBWithoutA() {

		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			B.create("b").persist(em);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Test
	public void testCanCreateInstanceOfBWithA() {

		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			A a = A.create("a1");
			B b = B.create("b1");
			C c = C.create("c1");
			c.setA(a);
			c.setB(b);
			c.setDescription("hello");
			a.getC().add(c);
			b.getC().add(c);
			em.persist(a);
			em.persist(b);
			em.persist(c);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Test(expected = RelationshipNotEstablishedException.class)
	public void testCannotCreateTwoLinksBetweenAAndB() {

		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			A a = A.create("a2").persist(em);
			B b = B.create("b2");
			C c = C.create("c2");
			c.setA(a);
			c.setB(b);
			c.setDescription("hello");
			a.getC().add(c);
			b.getC().add(c);
			em.persist(b);
			em.persist(c);
			C c3 = C.create("c3");
			c3.setA(a);
			c3.setB(b);
			c3.setDescription("hello");
			a.getC().add(c3);
			b.getC().add(c3);
			c3.persist(em);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}
}
