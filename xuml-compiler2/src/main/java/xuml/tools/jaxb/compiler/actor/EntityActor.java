package xuml.tools.jaxb.compiler.actor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import xuml.tools.jaxb.compiler.Entity;
import xuml.tools.jaxb.compiler.message.CloseEntityActor;
import xuml.tools.jaxb.compiler.message.EntityCommit;
import xuml.tools.jaxb.compiler.message.Signal;
import xuml.tools.jaxb.compiler.message.StopEntityActor;
import akka.actor.UntypedActor;

public class EntityActor extends UntypedActor {

	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;
	private boolean closed = false;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof EntityManagerFactory)
			handleMessage((EntityManagerFactory) message);
		else if (message instanceof Signal) {
			handleMessage((Signal) message);
		} else if (message instanceof EntityCommit) {
			commit((EntityCommit) message);
		} else if (message instanceof StopEntityActor) {
			getContext().stop(getSelf());
		}
	}

	@SuppressWarnings("unchecked")
	private void handleMessage(@SuppressWarnings("rawtypes") Signal signal) {
		if (closed) {
			// if this actor is marked as closed then bounce all signal messages
			// back to sender
			getSender().tell(signal);
		} else if (emf != null) {
			// otherwise perform the event on the entity after it has been
			// refreshed within the scope of the current entity manager
			checkTransaction();
			Entity<?, ?> entity = signal.getEntityEvent().getEntity();
			em.refresh(signal.getEntityEvent().getEntity());
			entity.event(signal.getEntityEvent().getEvent());
		}
	}

	private void handleMessage(EntityManagerFactory message) {
		this.emf = message;
	}

	private void commit(EntityCommit<?, ?> message) {
		if (em != null) {
			tx.commit();
			em.close();
			em = null;
			tx = null;
		}
		getSender().tell(new CloseEntityActor(message.getEntity()));
		closed = true;
	}

	private void checkTransaction() {
		if (em == null) {
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();
		}
	}
}
