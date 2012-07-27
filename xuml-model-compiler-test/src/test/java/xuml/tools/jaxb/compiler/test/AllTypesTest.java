package xuml.tools.jaxb.compiler.test;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import all_types.A;
import all_types.Context;

public class AllTypesTest {

	@BeforeClass
	public static void setup() {
		EntityManagerFactory emf = PersistenceHelper.createEmf("all-types");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		Context.close();
	}

	@Test
	public void testTypesAreGeneratedAsExpectedForEachMember() {
		EntityManager em = Context.createEntityManager();
		try {
			em.getTransaction().begin();
			A a = A.create("something");
			// boolean
			a.setOne(true);
			// integer
			a.setTwo(123);
			// real
			a.setThree(1.0001);
			// date
			a.setFour(new Date());
			// timestamp
			a.setFive(new Date());
			// arbitraryId
			a.setSix(10);

			a.persist(em);

			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

}
