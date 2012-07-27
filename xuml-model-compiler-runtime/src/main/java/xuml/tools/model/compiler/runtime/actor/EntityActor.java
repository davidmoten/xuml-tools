package xuml.tools.model.compiler.runtime.actor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.QueuedSignal;
import xuml.tools.model.compiler.runtime.message.CloseEntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;
import xuml.tools.model.compiler.runtime.message.StopEntityActor;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class EntityActor extends UntypedActor {

	private EntityManagerFactory emf;
	private boolean closed = false;
	private final LoggingAdapter log;
	private EntityActorListener listener = EntityActorListenerDoesNothing
			.getInstance();

	public EntityActor() {
		log = Logging.getLogger(getContext().system(), this);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		log.info("received message " + message.getClass().getName());
		if (message instanceof EntityManagerFactory)
			handleMessage((EntityManagerFactory) message);
		else if (message instanceof EntityActorListener)
			listener = (EntityActorListener) message;
		else if (message instanceof Signal) {
			handleMessage((Signal<?>) message);
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

			EntityManager em = null;
			EntityTransaction tx = null;
			Entity<?> entity = null;
			try {
				listener.beforeProcessing(entity, signal, this);
				em = emf.createEntityManager();
				tx = em.getTransaction();
				tx.begin();
				log.info("started transaction");
				entity = em.merge(signal.getEntity());
				log.info("merged");
				em.refresh(entity);
				log.info("calling event "
						+ signal.getEvent().getClass().getSimpleName()
						+ " on entity id = " + signal.getEntity().getId());
				entity.helper().setEntityManager(em);
				entity.event(signal.getEvent());
				entity.helper().setEntityManager(em);
				log.info("removing signal from persistence");
				em.createQuery(
						"delete from " + QueuedSignal.class.getSimpleName()
								+ " where id=:id")
						.setParameter("id", signal.getId()).executeUpdate();
				tx.commit();
				log.info("commited");
				listener.afterProcessing(entity, signal, this);
				em.close();
				// only after successful commit do we send the signals to other
				// entities made during onEntry procedure.
				entity.helper().sendQueuedSignals();
				closed = true;
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive())
					tx.rollback();
				if (em != null && em.isOpen())
					em.close();
				listener.failure(entity, signal, e, this);
			} finally {
				getSender().tell(new CloseEntityActor(signal.getEntity()));
			}
		}
	}

	private void handleMessage(EntityManagerFactory message) {
		this.emf = message;
	}

}
