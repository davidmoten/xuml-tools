package xuml.tools.model.compiler.runtime.actor;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.davidmoten.reels.Actor;
import com.github.davidmoten.reels.ActorRef;
import com.github.davidmoten.reels.Context;
import com.github.davidmoten.reels.MessageContext;
import com.google.common.collect.Maps;

import xuml.tools.model.compiler.runtime.SignalProcessorListenerFactory;
import xuml.tools.model.compiler.runtime.message.CloseEntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;
import xuml.tools.model.compiler.runtime.message.StopEntityActor;

public class RootActor implements Actor<Object> {

    private static final Logger log = LoggerFactory.getLogger(RootActor.class);

    private EntityManagerFactory emf;
    private final HashMap<String, ActorInfo> actors = Maps.newHashMap();
    private SignalProcessorListenerFactory listenerFactory;

    public RootActor() {
    }

    @Override
    public void onMessage(MessageContext<Object> context, Object message) {
        log.debug("received message {}", message.getClass().getName());
        if (message instanceof EntityManagerFactory)
            handleMessage((EntityManagerFactory) message);
        else if (message instanceof SignalProcessorListenerFactory)
            listenerFactory = (SignalProcessorListenerFactory) message;
        else if (message instanceof Signal)
            handleMessage(context, (Signal<?>) message);
        else if (message instanceof CloseEntityActor)
            handleMessage(context, (CloseEntityActor) message);
    }

    private void handleMessage(MessageContext<Object> context, CloseEntityActor message) {
        String key = message.getEntityUniqueId();
        ActorInfo info = actors.remove(key);
        if (info.counter > 1) {
            actors.put(key, info.decrement());
        } else {
            // when the counter gets down to 1 we stop the entity actor
            info.actor.tell(new StopEntityActor(), context.self());
        }
    }

    private void handleMessage(EntityManagerFactory message) {
        emf = message;
    }

    private void handleMessage(MessageContext<Object> context, Signal<?> message) {
        String key = message.getEntityUniqueId();
        ActorRef<Object> actor = getActor(context, key);
        actor.tell(message, context.self());
    }

    private ActorRef<Object> getActor(MessageContext<Object> context, String key) {
        ActorInfo info = actors.get(key);
        if (info == null) {
            ActorRef<Object> actor = createActor(context.context(), key);
            actors.put(key, new ActorInfo(actor, 1));
            actor.tell(emf, context.self());
            if (listenerFactory != null)
                actor.tell(listenerFactory.create(key), context.self());
        } else {
            actors.put(key, info.increment());
        }
        return actors.get(key).actor;
    }

    private ActorRef<Object> createActor(Context context, String key) {
        return context.createActor(EntityActor.class);
    }

    private static final class ActorInfo {

        final ActorRef<Object> actor;
        final long counter;

        ActorInfo(ActorRef<Object> actor, long counter) {
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
