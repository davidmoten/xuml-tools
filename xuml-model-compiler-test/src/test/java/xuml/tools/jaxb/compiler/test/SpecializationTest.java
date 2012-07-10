package xuml.tools.jaxb.compiler.test;

import static javax.persistence.Persistence.createEntityManagerFactory;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import specialization.A;
import specialization.B;
import specialization.Context;
import xuml.tools.util.database.DerbyUtil;

public class SpecializationTest {

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		Context.setEntityManagerFactory(createEntityManagerFactory("specialization"));
	}

	@AfterClass
	public static void close() {
		Context.stop();
		Context.close();
	}

	@Test
	public void testCanCreate() {
		EntityManager em = Context.createEntityManager();
		em.getTransaction().begin();
		B b = B.create("hello");
		A a = A.create("something");
		a.setTwo(2);
		a.setB(b);
		b.setA(a);
		b.setNumber(3);
		a.persist(em);
		b.persist(em);
		em.getTransaction().commit();
	}

	// TODO cannot create A without a B or a C

	// TODO cannot create A with both B and C
}
