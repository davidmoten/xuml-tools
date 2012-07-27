package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import specialization.A;
import specialization.B;
import specialization.C;
import specialization.Context;
import xuml.tools.model.compiler.runtime.RelationshipNotEstablishedException;
import xuml.tools.model.compiler.runtime.TooManySpecializationsException;

public class SpecializationTest {

	@BeforeClass
	public static void setup() {
		Context.setEntityManagerFactory(PersistenceHelper
				.createEmf("specialization"));
	}

	@AfterClass
	public static void close() {
		Context.stop();
		Context.close();
	}

	@Test
	public void testCanCreate() {
		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			A a = A.create("something");
			B b = B.create("hello");
			a.setTwo(2);
			a.setB(b);
			b.setA(a);
			b.setNumber(3);
			a.persist(em);
			b.persist(em);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Test(expected = RelationshipNotEstablishedException.class)
	public void testCannotCreateAWithoutOneSpecialization() {
		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			A a = A.create("something2");
			a.setTwo(2);
			a.persist(em);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Test(expected = TooManySpecializationsException.class)
	public void testCannotCreateAWithTwoSpecializations() {
		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			A a = A.create("something3");
			B b = B.create("hello3");
			C c = C.create("there3");
			a.setTwo(2);
			a.setB(b);
			a.setC(c);
			c.setA(a);
			b.setA(a);
			b.setNumber(3);
			a.persist(em);
			b.persist(em);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	// TODO cannot create A with both B and C
}
