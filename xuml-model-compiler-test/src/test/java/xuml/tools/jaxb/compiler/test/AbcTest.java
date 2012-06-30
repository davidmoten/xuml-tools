package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import moten.david.util.database.derby.DerbyUtil;

import org.junit.Assert;
import org.junit.Test;

import xuml.tools.jaxb.compiler.actor.Signaller;
import abc.A;
import abc.A.AId;
import abc.A.Events.Create;
import abc.A.Events.SomethingDone;
import abc.behaviour.ABehaviour;
import abc.behaviour.ABehaviourFactory;

public class AbcTest {

	@Test
	public void testCreateEntityManagerFactoryAndCreateAndPersistOneEntity()
			throws InterruptedException {
		DerbyUtil.disableDerbyLog();
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("abc");
		Signaller.getInstance().setEntityManagerFactory(emf);

		A.setBehaviourFactory(new ABehaviourFactory() {
			@Override
			public ABehaviour create(A cls) {
				return new ABehaviour() {
					@Override
					public void onEntryDoneSomething(A entity,
							SomethingDone event) {
						entity.setAThree(event.getTheCount());
						System.out.println(event.getTheCount());
					}

					@Override
					public void onEntryHasStarted(A entity, Create event) {
						AId id = new AId();
						id.setAOne(event.getAOne());
						id.setATwo(event.getATwo());
						entity.setId(id);
						entity.setAThree(event.getAccountNumber());
						System.out.println("created");
					}
				};
			}
		});

		EntityManager em = emf.createEntityManager();

		Create create = new A.Events.Create("value1", "value2", "1234");

		A a = new A();
		a.event(create);
		em.persist(a);
		em.close();

		a.signal(new A.Events.SomethingDone("12a"));
		Thread.sleep(5000);

		em = emf.createEntityManager();
		em.merge(a);
		Assert.assertEquals("12a", a.getAThree());
		em.close();

		Signaller.getInstance().stop();

	}

	@Test(expected = NullPointerException.class)
	public void testBehaviourNotSetForAThrowsException() {
		A.setBehaviourFactory(null);
		new A();
	}
}
