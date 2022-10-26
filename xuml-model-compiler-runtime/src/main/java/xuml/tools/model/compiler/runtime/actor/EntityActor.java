package xuml.tools.model.compiler.runtime.actor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.davidmoten.reels.AbstractActor;
import com.github.davidmoten.reels.Message;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.QueuedSignal;
import xuml.tools.model.compiler.runtime.SignalProcessorListener;
import xuml.tools.model.compiler.runtime.SignalProcessorListenerDoesNothing;
import xuml.tools.model.compiler.runtime.message.CloseEntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;
import xuml.tools.model.compiler.runtime.message.StopEntityActor;

public class EntityActor extends AbstractActor<Object> {

    private static final Logger log = LoggerFactory.getLogger(EntityActor.class);
    
    private EntityManagerFactory emf;
    private SignalProcessorListener listener = SignalProcessorListenerDoesNothing.getInstance();

    private Message<Object> m;

    public EntityActor() {
    }

    @Override
    public void onMessage(Message<Object> m) {
        this.m = m;
        Object message = m.content();
        log.debug("received message {}", message.getClass().getName());
        if (message instanceof EntityManagerFactory)
            handleMessage((EntityManagerFactory) message);
        else if (message instanceof SignalProcessorListener)
            listener = (SignalProcessorListener) message;
        else if (message instanceof Signal) {
            handleMessage((Signal<?>) message);
        } else if (message instanceof StopEntityActor) {
            m.self().stop();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleMessage(@SuppressWarnings("rawtypes") Signal signal) {
        if (emf != null) {
            // perform the event on the entity after it has been
            // loaded by a new EntityManager
            EntityManager em = null;
            EntityTransaction tx = null;
            Entity<?> entity = null;
            try {
                listener.beforeProcessing(signal, this);
                em = emf.createEntityManager();
                tx = em.getTransaction();
                tx.begin();
                log.debug("started transaction");
                entity = (Entity<?>) em.find(signal.getEntityClass(), signal.getEntityId());
                log.debug("calling event {} on entity id = ",
                        signal.getEvent().getClass().getSimpleName(), signal.getEntityId());
                entity.helper().setEntityManager(em);
                entity.event(signal.getEvent());
                log.debug("removing signal from persistence signalId={}, entityId={}",
                        signal.getId(), signal.getEntityId());
                int countDeleted = em
                        .createQuery("delete from " + QueuedSignal.class.getSimpleName()
                                + " where id=:id")
                        .setParameter("id", signal.getId()).executeUpdate();
                if (countDeleted == 0) {
                    throw new RuntimeException("queued signal not deleted: " + signal.getId());
                }
                tx.commit();
                log.debug("committed");
                listener.afterProcessing(signal, this);
                em.close();
                entity.helper().setEntityManager(null);
                // only after successful commit do we send the signals to other
                // entities made during onEntry procedure.
                entity.helper().sendQueuedSignals();
            } catch (RuntimeException e) {
                handleException(signal, em, tx, e);
            } finally {
                // in case this entity is reused make sure its entity manager is
                // cleared
                if (entity != null) {
                    entity.helper().setEntityManager(null);
                }
                // give RootActor a chance to dispose of this actor
                m.<Object>sender().tell(new CloseEntityActor(signal.getEntityUniqueId()), m.self());
            }
        }
    }

    private void handleException(@SuppressWarnings("rawtypes") Signal signal, EntityManager em,
            EntityTransaction tx, RuntimeException e) {
        try {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (em != null && em.isOpen()) {
                em.close();
            }
            listener.failure(signal, e, this);
        } catch (RuntimeException e2) {
            log.error(e2.getMessage(), e2);
            throw e;
        }
    }

    private void handleMessage(EntityManagerFactory message) {
        this.emf = message;
    }

}
