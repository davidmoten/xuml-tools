package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import many_to_many.A;
import many_to_many.B;
import many_to_many.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BinaryAssociationManyToManyTest {

	@BeforeClass
	public static void setup() {
		EntityManagerFactory emf = PersistenceHelper.createEmf("many-to-many");

		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		Context.close();
	}

	@Test
	public void testCanCreateManyToMany() {

		EntityManager em = Context.createEntityManager();
		em.getTransaction().begin();
		A a1 = A.create("thing").persist(em);
		A a2 = A.create("thing2").persist(em);
		B b1 = B.create("boo").persist(em);
		B b2 = B.create("boo2").persist(em);
		a1.getB_R1().add(b1);
		a1.getB_R1().add(b2);
		b1.getA_R1().add(a1);
		b2.getA_R1().add(a1);

		a2.getB_R1().add(b1);
		a2.getB_R1().add(b2);
		b1.getA_R1().add(a2);
		b2.getA_R1().add(a2);

		em.getTransaction().commit();
		em.close();
	}
}
