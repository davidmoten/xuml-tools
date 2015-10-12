package xuml.tools.model.compiler.runtime.actor;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;

import com.google.common.collect.Maps;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.RoundRobinPool;
import xuml.tools.model.compiler.runtime.SignalProcessorListenerFactory;
import xuml.tools.model.compiler.runtime.message.ActorConfig;
import xuml.tools.model.compiler.runtime.message.CloseEntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;
import xuml.tools.model.compiler.runtime.message.StopEntityActor;

public class RootActor extends UntypedActor {

    private EntityManagerFactory emf;
    private final HashMap<String, ActorInfo> actors = Maps.newHashMap();
    private final LoggingAdapter log;
    private SignalProcessorListenerFactory listenerFactory;
    private RoundRobinPool pool;

    public RootActor() {
        log = Logging.getLogger(getContext().system(), this);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        log.debug("received message {}", message.getClass().getName());
        if (message instanceof ActorConfig) {
            handleMessage((ActorConfig) message);
        } else if (message instanceof EntityManagerFactory)
            handleMessage((EntityManagerFactory) message);
        else if (message instanceof SignalProcessorListenerFactory)
            listenerFactory = (SignalProcessorListenerFactory) message;
        else if (message instanceof Signal)
            handleMessage((Signal<?>) message);
        else if (message instanceof CloseEntityActor)
            handleMessage((CloseEntityActor) message);
    }

    private void handleMessage(ActorConfig message) {
        pool = new RoundRobinPool(message.getEntityActoryPoolSize());
    }

    private void handleMessage(CloseEntityActor message) {
        String key = message.getEntityUniqueId();
        ActorInfo info = actors.remove(key);
        if (info.counter > 1) {
            actors.put(key, info.decrement());
        } else {
            // when the counter gets down to 1 we stop the entity actor
            info.actor.tell(new StopEntityActor(), getSelf());
        }
    }

    private void handleMessage(EntityManagerFactory message) {
        emf = message;
    }

    private void handleMessage(Signal<?> message) {
        String key = message.getEntityUniqueId();
        ActorRef actor = getActor(key);
        actor.tell(message, getSelf());
    }

    private ActorRef getActor(String key) {
        ActorInfo info = actors.get(key);
        if (info == null) {
            ActorRef actor = createActor(key);
            actors.put(key, new ActorInfo(actor, 1));
            actor.tell(emf, getSelf());
            if (listenerFactory != null)
                actor.tell(listenerFactory.create(key), getSelf());
        } else {
            actors.put(key, info.increment());
        }
        return actors.get(key).actor;
    }

    private ActorRef createActor(String key) {
        return getContext().actorOf(pool.props(Props.create(EntityActor.class))
                .withDispatcher("my-thread-pool-dispatcher"), key);
    }

    private static final class ActorInfo {

        final ActorRef actor;
        final long counter;

        ActorInfo(ActorRef actor, long counter) {
            this.actor = actor;
            this.counter = counter;
        }

        ActorInfo increment() {
            return new ActorInfo(actor, counter + 1);
        }

        ActorInfo decrement() {
            return new ActorInfo(actor, counter - 1);
        }

    }
}
