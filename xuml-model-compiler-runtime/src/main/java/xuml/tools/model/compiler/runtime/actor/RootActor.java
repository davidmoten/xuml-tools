package xuml.tools.model.compiler.runtime.actor;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.davidmoten.reels.AbstractActor;
import com.github.davidmoten.reels.ActorRef;
import com.github.davidmoten.reels.Context;
import com.github.davidmoten.reels.Message;
import com.google.common.collect.Maps;

import xuml.tools.model.compiler.runtime.SignalProcessorListenerFactory;
import xuml.tools.model.compiler.runtime.message.CloseEntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;
import xuml.tools.model.compiler.runtime.message.StopEntityActor;

public class RootActor extends AbstractActor<Object> {

    private static final Logger log = LoggerFactory.getLogger(RootActor.class);

    private EntityManagerFactory emf;
    private final HashMap<String, ActorInfo> actors = Maps.newHashMap();
    private SignalProcessorListenerFactory listenerFactory;

    public RootActor() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(Message<Object> message) {
        Object content = message.content();
        log.debug("received message {}", content.getClass().getName());
        if (content instanceof EntityManagerFactory)
            handleMessage((EntityManagerFactory) content);
        else if (content instanceof SignalProcessorListenerFactory)
            listenerFactory = (SignalProcessorListenerFactory) content;
        else if (content instanceof Signal)
            handleSignalMessage((Message<Signal<?>>)(Message<?>) message);
        else if (content instanceof CloseEntityActor)
            handleMessage((Message<CloseEntityActor>)(Message<?>) message);
    }

    private void handleMessage( Message<CloseEntityActor> message) {
        CloseEntityActor content = message.content();
        String key = content.getEntityUniqueId();
        ActorInfo info = actors.remove(key);
        if (info.counter > 1) {
            actors.put(key, info.decrement());
        } else {
            // when the counter gets down to 1 we stop the entity actor
            info.actor.tell(new StopEntityActor(), message.self());
        }
    }

    private void handleMessage(EntityManagerFactory message) {
        emf = message;
    }

    private void handleSignalMessage(Message<Signal<?>> message) {
        String key = message.content().getEntityUniqueId();
        ActorRef<Object> actor = getActor(message, key);
        actor.tell(message, message.self());
    }

    private ActorRef<Object> getActor(Message<?> message, String key) {
        ActorInfo info = actors.get(key);
        if (info == null) {
            ActorRef<Object> actor = createActor(message.context(), key);
            actors.put(key, new ActorInfo(actor, 1));
            actor.tell(emf, message.self());
            if (listenerFactory != null)
                actor.tell(listenerFactory.create(key), message.self());
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
