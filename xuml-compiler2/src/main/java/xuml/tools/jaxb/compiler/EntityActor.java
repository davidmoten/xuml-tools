package xuml.tools.jaxb.compiler;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import xuml.tools.jaxb.compiler.message.Commit;
import akka.actor.UntypedActor;

public class EntityActor<T extends Entity<T, R>, R> extends UntypedActor {

	private final Class<T> cls;
	private final EntityManagerFactory emf;
	private final R id;
	private EntityManager em;
	private EntityTransaction tx;

	public EntityActor(EntityManagerFactory emf, Class<T> cls, R id) {
		this.emf = emf;
		this.cls = cls;
		this.id = id;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof SignalToOther) {
			handleSignalToOther(message);
		} else if (message instanceof Commit) {
			handleCommit();
		}
	}

	private void handleSignalToOther(Object message) {
		@SuppressWarnings("unchecked")
		SignalToOther<Event<T>> signal = (SignalToOther<Event<T>>) message;
		try {
			Event<T> event = signal.getEvent();
			em = emf.createEntityManager();
			tx = em.getTransaction();
			T t = em.find(cls, id);
			tx.begin();
			t.event(event);
			getSelf().tell(new Commit());
		} catch (RuntimeException e) {
			getContext().system().log().error(e.getMessage(), e);
		}
	}

	private void handleCommit() {
		tx.commit();
		em.close();
	}
}
