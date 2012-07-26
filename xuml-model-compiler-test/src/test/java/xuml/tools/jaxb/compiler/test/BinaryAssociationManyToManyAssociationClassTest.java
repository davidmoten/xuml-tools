package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import many_to_many_association.A;
import many_to_many_association.B;
import many_to_many_association.C;
import many_to_many_association.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;

public class BinaryAssociationManyToManyAssociationClassTest {

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("many-to-many-association");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		Context.close();
	}

	// TODO many more tests can be added here

	@Test
	public void testCanCreateManyToMany() {

		EntityManager em = Context.createEntityManager();
		em.getTransaction().begin();
		A a1 = A.create("thing").persist(em);
		B b1 = B.create("boo").persist(em);
		B b2 = B.create("boo2").persist(em);
		C c1 = new C("c1");
		c1.setDescription("example");
		c1.setA(a1);
		c1.setB(b1);
		a1.getC().add(c1);
		b1.getC().add(c1);

		C c2 = new C("c2");
		c2.setDescription("example2");
		c2.setA(a1);
		c2.setB(b2);
		a1.getC().add(c2);
		b2.getC().add(c2);

		c1.persist(em);
		c2.persist(em);
		em.getTransaction().commit();

		em.close();

		em = Context.createEntityManager();
		assertEquals(2, a1.load(em).getB().size());

		em.close();
	}
}
