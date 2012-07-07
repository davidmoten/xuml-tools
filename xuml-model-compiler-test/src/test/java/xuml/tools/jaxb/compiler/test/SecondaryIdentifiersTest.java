package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import secondary_identifiers.A;
import secondary_identifiers.Context;
import xuml.tools.util.database.DerbyUtil;

public class SecondaryIdentifiersTest {
	private static EntityManagerFactory emf;

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		emf = Persistence.createEntityManagerFactory("secondary-identifiers");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		emf.close();
	}

	@Test(expected = PersistenceException.class)
	public void testCreateAWithNullSecondaryIdentifiersShouldFail() {

		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			A.create("boo").persist(em);
			// any participant in an identifier should not be nullable so must
			// set them. Not setting them should throw an exception.
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Test
	public void testCreateWithAllIdentifiersSpecified() {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			A a = A.create("one");
			a.setATwo("two");
			a.setAThree("three");
			a.setAFour("four");
			a.setAFive("five");
			a.persist(em);
			em.getTransaction().commit();
		} finally {
			em.close();
		}

	}

	@Test(expected = ConstraintViolationException.class)
	public void testPersistWithNonUniqueSecondaryIdentifiersFails() {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			{
				A a = A.create("one2");
				a.setATwo("two2");
				a.setAThree("three2");
				a.setAFour("four2");
				a.setAFive("five2");
				a.persist(em);
			}
			{
				A a = A.create("one3");
				a.setATwo("two2");
				a.setAThree("three2");
				a.setAFour("four3");
				a.setAFive("five3");
				a.persist(em);
			}
			em.getTransaction().commit();
			Assert.fail();
		} catch (RollbackException e) {
			throw (RuntimeException) e.getCause().getCause();
		} finally {
			em.close();
		}

	}

}
