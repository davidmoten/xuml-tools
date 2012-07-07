package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import one_to_zero_one.A;
import one_to_zero_one.A.AId;
import one_to_zero_one.B;
import one_to_zero_one.B.BId;
import one_to_zero_one.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.model.compiler.runtime.Signaller;
import xuml.tools.util.database.DerbyUtil;

public class BinaryAssociationOneToZeroOneTest {

	private static EntityManagerFactory emf;

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		emf = Persistence.createEntityManagerFactory("one-to-zero-one");
		Context.setEntityManagerFactory(emf);
	}

	@AfterClass
	public static void shutdown() {
		emf.close();
	}

	@Test
	public void testCreateAWithoutB() {

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		A.create(new A.AId("hello", "there")).persist(em);
		em.getTransaction().commit();
		em.close();
	}

	@Test
	public void testSignalPersistence() throws ClassNotFoundException {

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		A.create(new A.AId("hello2", "there2")).persist(em);
		em.getTransaction().commit();

		// now test can find using id
		em.getTransaction().begin();
		assertNotNull(em.find(A.class, new A.AId("hello2", "there2")));
		em.getTransaction().commit();

		// now test can find using id and Class.forName
		em.getTransaction().begin();
		assertNotNull(em.find(Class.forName(A.class.getName()), new A.AId(
				"hello2", "there2")));
		em.getTransaction().commit();

		em.close();

		// now test can find using new em, Class.forName and reconstituded id
		em = emf.createEntityManager();
		em.getTransaction().begin();
		AId id = new A.AId("hello2", "there2");
		Object id2 = Signaller.toObject(Signaller.toBytes(id));
		assertNotNull(em.find(Class.forName(A.class.getName()), id2));
		em.getTransaction().commit();

		// Context.persistSignal(a3.getId(), (Class<Entity<A>>) a3.getClass(),
		// new A.Events.SomethingDone("12c"));
		// Context.sendSignalsInQueue();

		em.close();
	}

	@Test(expected = PersistenceException.class)
	public void testCannotCreateBWithoutA() {

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			B.create(new BId("some", "thing")).persist(em);
		} finally {
			em.getTransaction().rollback();
			em.close();
		}
	}

	@Test
	public void testCreateAWithBAndIsPersistedProperly() {
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			A a2 = A.create(new AId("boo", "baa"));
			B b = B.create(new BId("some2", "thing2"));
			b.setA(a2);
			em.persist(b);
			em.getTransaction().commit();
			em.close();
		}
		{
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			A a2 = em.find(A.class, new A.AId("boo", "baa"));
			assertNotNull(a2.getB());
			em.getTransaction().commit();
			em.close();
		}
	}

}
