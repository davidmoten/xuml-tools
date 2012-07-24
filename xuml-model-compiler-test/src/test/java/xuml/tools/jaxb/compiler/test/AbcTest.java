package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;
import abc.A;
import abc.A.AId;
import abc.A.BehaviourFactory;
import abc.A.Events.Create;
import abc.A.Events.StateSignature_DoneSomething;
import abc.Context;

public class AbcTest {

	/**
	 * Demonstrates the major aspects of using entities generated by
	 * xuml-model-compiler including reloading signals not fully processed at
	 * time of last shutdown, creation of entities and asynchronous signalling.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testCreateEntityManagerFactoryAndCreateAndSignalEntities()
			throws InterruptedException {

		// Note that the classes Context and A are generated from xml using
		// the xuml-tools-maven-plugin

		// see the setup() method below for how the Context is initialized.

		// send any signals not processed from last shutdown
		Context.sendSignalsInQueue();

		// create some entities (this happens synchronously)
		A a1 = Context.create(A.class, new A.Events.Create("value1.1",
				"value2.1", "1234"));
		A a2 = Context.create(A.class, new A.Events.Create("value1.2",
				"value2.2", "1234"));
		A a3 = Context.create(A.class, new A.Events.Create("value1.3",
				"value2.3", "1234"));

		// send asynchronous signals to the a2 and a3
		a1.signal(new A.Events.SomethingDone(11));
		a2.signal(new A.Events.SomethingDone(12));
		a3.signal(new A.Events.SomethingDone(13));

		// wait a bit for all signals to be processed
		Thread.sleep(2000);

		// Check the signals were processed

		// The load method reloads the entity using a fresh entity manager and
		// then closes the entity manager. As a consequence only non-proxied
		// fields of the entity will be retrievable using the load method.
		assertEquals("11", a1.load().getAThree());
		assertEquals("12", a2.load().getAThree());
		assertEquals("13", a3.load().getAThree());

		// Notice that all the above could be done without explicitly creating
		// EntityManagers at all. Nice!

		// Refresh the entities from the database using the
		// load(em) method and check the signals were processed
		EntityManager em = Context.createEntityManager();
		// note that the load method below does an em merge and refresh and
		// returns a new entity for use within the current entity manager
		assertEquals("11", a1.load(em).getAThree());
		assertEquals("12", a2.load(em).getAThree());
		assertEquals("13", a3.load(em).getAThree());
		em.close();

	}

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();

		// create the entity manager factory
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("abc");

		// pass the EntityManagerFactory to the generated xuml Context
		Context.setEntityManagerFactory(emf);

		// set the behaviour factory for the class A
		A.setBehaviourFactory(createBehaviourFactory());
	}

	@AfterClass
	public static void cleanup() {

		// shutdown the actor system
		Context.stop();

		// close the entity manager factory if desired
		Context.close();
	}

	@Test
	public void testEntitiesAndSignalsArePersisted()
			throws InterruptedException {

		A a = Context.create(A.class, new A.Events.Create("value1.4",
				"value2.4", "1234"));
		// check that the entity was persisted
		{
			EntityManager em = Context.createEntityManager();
			Assert.assertNotNull(em.find(A.class, new A.AId("value1.4",
					"value2.4")));
			em.close();
		}

		// test the reloading of a persisted signal to a1
		Context.persistSignal(a.getId(), A.class,
				new A.Events.SomethingDone(14));
		assertEquals(1, Context.sendSignalsInQueue());
		// wait a bit for all signals to be processed
		Thread.sleep(2000);

		// Refresh the entities from the database using the
		// load method and check the signals were processed
		EntityManager em = Context.createEntityManager();
		// note that the load method below does an em merge and refresh and
		// returns a new entity for use within the current entity manager
		assertEquals("14", a.load(em).getAThree());
		em.close();

	}

	private static BehaviourFactory createBehaviourFactory() {
		return new A.BehaviourFactory() {
			@Override
			public A.Behaviour create(final A entity) {
				return new A.Behaviour() {

					@Override
					public void onEntryHasStarted(Create event) {
						entity.setId(new AId(event.getAOne(), event.getATwo()));
						entity.setAThree(event.getAccountNumber());
						System.out.println("created");
					}

					@Override
					public void onEntryDoneSomething(
							StateSignature_DoneSomething event) {
						entity.setAThree(event.getTheCount() + "");
						System.out
								.println("setting A.athree="
										+ entity.getAThree() + " for "
										+ entity.getId());
						// demonstrate/unit test getting access to the current
						// entity manager when needed
						Object count = Context.em()
								.createQuery("select count(*) from B b")
								.getResultList();
						System.out.println("counted " + count + " B entities");
					}
				};
			}
		};
	}

	@Test(expected = NullPointerException.class)
	public void testBehaviourNotSetForAThrowsException() {
		A.BehaviourFactory f = A.getBehaviourFactory();
		try {
			A.setBehaviourFactory(null);
			new A();
		} finally {
			A.setBehaviourFactory(f);
		}
	}

}
