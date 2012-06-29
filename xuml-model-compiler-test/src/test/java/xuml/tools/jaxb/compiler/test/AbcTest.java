package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import moten.david.util.database.derby.DerbyUtil;

import org.junit.Test;

import abc.A;
import abc.A.AId;
import abc.A.Events.SomethingDone;
import abc.behaviour.ABehaviour;
import abc.behaviour.ABehaviourFactory;

public class AbcTest {

	@Test
	public void test() {
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
				};
			}
		});
		A a = new A();
		AId id = new AId();
		id.setAOne("value1");
		id.setATwo("value2");
		a.setId(id);
		em.persist(a);
		em.close();
	}

	@Test(expected = NullPointerException.class)
	public void testBehaviourNotSetForAThrowsException() {
		new A();
	}
}
