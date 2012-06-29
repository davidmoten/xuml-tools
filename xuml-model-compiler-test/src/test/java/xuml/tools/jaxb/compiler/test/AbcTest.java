package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import moten.david.util.database.derby.DerbyUtil;

import org.junit.Test;

import abc.A;
import abc.A.AId;
import abc.A.Events.Create;
import abc.A.Events.SomethingDone;
import abc.behaviour.ABehaviour;
import abc.behaviour.ABehaviourFactory;

public class AbcTest {

	@Test
	public void testCreateEntityManagerFactoryAndCreateAndPersistOneEntity() {
		DerbyUtil.disableDerbyLog();
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("abc");
		EntityManager em = emf.createEntityManager();
		A.setBehaviourFactory(new ABehaviourFactory() {
			@Override
			public ABehaviour create(A cls) {
				return new ABehaviour() {
					@Override
					public void onEntryDoneSomething(A entity,
							SomethingDone event) {
						entity.setAThree("done something");
					}

					@Override
					public void onEntryHasStarted(A entity, Create event) {
						AId id = new AId();
						id.setAOne(event.getAOne());
						id.setATwo(event.getATwo());
						entity.setId(id);
						entity.setAThree(event.getAccountNumber());
					}
				};
			}
		});
		A a = new A();

		Create create = new A.Events.Create("value1", "value2", "1234");
		a.event(create);
		em.persist(a);
		em.close();
	}

	@Test(expected = NullPointerException.class)
	public void testBehaviourNotSetForAThrowsException() {
		new A();
	}
}
