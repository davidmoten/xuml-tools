package xuml.tools.model.compiler.runtime.actor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.QueuedSignal;
import xuml.tools.model.compiler.runtime.SignalProcessorListener;
import xuml.tools.model.compiler.runtime.SignalProcessorListenerDoesNothing;
import xuml.tools.model.compiler.runtime.message.CloseEntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;
import xuml.tools.model.compiler.runtime.message.StopEntityActor;

public class EntityActor extends UntypedActor {

    private EntityManagerFactory emf;
    private final LoggingAdapter log;
    private SignalProcessorListener listener = SignalProcessorListenerDoesNothing.getInstance();

    public EntityActor() {
        log = Logging.getLogger(getContext().system(), this);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        log.debug("received message {}", message.getClass().getName());
        if (message instanceof EntityManagerFactory)
            handleMessage((EntityManagerFactory) message);
        else if (message instanceof SignalProcessorListener)
            listener = (SignalProcessorListener) message;
        else if (message instanceof Signal) {
            handleMessage((Signal<?>) message);
        } else if (message instanceof StopEntityActor) {
            getContext().stop(getSelf());
        }
    }

    @SuppressWarnings("unchecked")
    private void handleMessage(@SuppressWarnings("rawtypes") Signal signal) {
        if (emf != null) {
            // otherwise perform the event on the entity after it has been
            // refreshed within the scope of the current entity manager

            EntityManager em = null;
            EntityTransaction tx = null;
            try {
                listener.beforeProcessing(signal, this);
                em = emf.createEntityManager();
                tx = em.getTransaction();
                tx.begin();
                log.debug("started transaction");
                Entity<?> en;
                // TODO remove commented out code
                // synchronized (signal) {
                // // because signal is not an immutable object make sure we
                // // have its latest values
                // en = signal.getEntity();
                // }
                // entity = em.merge(en);
                // log.debug("merged");
                // em.refresh(entity);
                en = (Entity<?>) em.find(signal.getEntityClass(), signal.getEntityId());
                log.debug("calling event {} on entity id = ",
                        signal.getEvent().getClass().getSimpleName(), signal.getEntityId());
                en.helper().setEntityManager(em);
                en.event(signal.getEvent());
                // en.helper().setEntityManager(em);
                log.debug("removing signal from persistence signalId={}, entityId={}",
                        signal.getId(), signal.getEntityId());
                int countDeleted = em
                        .createQuery("delete from " + QueuedSignal.class.getSimpleName()
                                + " where id=:id")
                        .setParameter("id", signal.getId()).executeUpdate();
                if (countDeleted == 0)
                    throw new RuntimeException("queued signal not deleted: " + signal.getId());
                tx.commit();
                log.debug("committed");
                listener.afterProcessing(signal, this);
                em.close();
                // TODO do this in finally?
                en.helper().setEntityManager(null);
                // only after successful commit do we send the signals to other
                // entities made during onEntry procedure.
                en.helper().sendQueuedSignals();
            } catch (RuntimeException e) {
                try {
                    if (tx != null && tx.isActive())
                        tx.rollback();
                    if (em != null && em.isOpen())
                        em.close();
                    listener.failure(signal, e, this);
                } catch (RuntimeException e2) {
                    log.error(e2.getMessage(), e2);
                    throw e;
                }
            } finally {
                // give RootActor a chance to dispose of this actor
                getSender().tell(new CloseEntityActor(signal.getEntityUniqueId()), getSelf());
            }
        }
    }

    private void handleMessage(EntityManagerFactory message) {
        this.emf = message;
    }

}
