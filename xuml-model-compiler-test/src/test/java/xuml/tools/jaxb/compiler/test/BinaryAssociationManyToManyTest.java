package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import many_to_many.A;
import many_to_many.B;
import many_to_many.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import xuml.tools.util.database.DerbyUtil;

public class BinaryAssociationManyToManyTest {

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("many-to-many");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		Context.close();
	}

	// @Test
	public void testCanCreateManyToMany() {

		EntityManager em = Context.createEntityManager();
		em.getTransaction().begin();
		A a1 = A.create("thing").persist(em);
		A a2 = A.create("thing2").persist(em);
		B b1 = B.create("boo").persist(em);
		B b2 = B.create("boo2").persist(em);
		a1.getB().add(b1);
		a1.getB().add(b2);
		b1.getA().add(a1);
		b2.getA().add(a1);

		a2.getB().add(b1);
		a2.getB().add(b2);
		b1.getA().add(a2);
		b2.getA().add(a2);

		em.getTransaction().commit();
		em.close();
	}
}
