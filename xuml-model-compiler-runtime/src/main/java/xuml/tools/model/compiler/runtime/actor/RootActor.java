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

    private ActorRef<Object> self;

    private Context context;

    public RootActor() {
    }

    @Override
    public void onMessage(Message<Object> m) {
        this.self = m.self();
        this.context = m.context();
        Object message = m.content();
        log.debug("received message {}", message.getClass().getName());
        if (message instanceof EntityManagerFactory)
            handleMessage((EntityManagerFactory) message);
        else if (message instanceof SignalProcessorListenerFactory)
            listenerFactory = (SignalProcessorListenerFactory) message;
        else if (message instanceof Signal)
            handleMessage((Signal<?>) message);
        else if (message instanceof CloseEntityActor)
            handleMessage((CloseEntityActor) message);
    }

    private void handleMessage(CloseEntityActor message) {
        String key = message.getEntityUniqueId();
        ActorInfo info = actors.remove(key);
        if (info.counter > 1) {
            actors.put(key, info.decrement());
        } else {
            // when the counter gets down to 1 we stop the entity actor
            info.actor.tell(new StopEntityActor(), self);
        }
    }

    private void handleMessage(EntityManagerFactory message) {
        emf = message;
    }

    private void handleMessage(Signal<?> message) {
        String key = message.getEntityUniqueId();
        ActorRef<Object> actor = getActor(key);
        actor.tell(message, self);
    }

    private ActorRef<Object> getActor(String key) {
        ActorInfo info = actors.get(key);
        if (info == null) {
            ActorRef<Object> actor = createActor(key);
            actors.put(key, new ActorInfo(actor, 1));
            actor.tell(emf, self);
            if (listenerFactory != null)
                actor.tell(listenerFactory.create(key), self);
        } else {
            actors.put(key, info.increment());
        }
        return actors.get(key).actor;
    }

    private ActorRef<Object> createActor(String key) {
        return context
                .factory(() -> new EntityActor()).build();
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
