package xuml.tools.model.compiler.runtime.actor;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.message.CloseEntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;
import xuml.tools.model.compiler.runtime.message.StopEntityActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.google.common.collect.Maps;

public class RootActor extends UntypedActor {

	private EntityManagerFactory emf;
	private final HashMap<String, ActorRef> actors = Maps.newHashMap();
	private final LoggingAdapter log;

	public RootActor() {
		log = Logging.getLogger(getContext().system(), this);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		log.info("received message " + message.getClass().getName());
		if (message instanceof EntityManagerFactory)
			handleMessage((EntityManagerFactory) message);
		else if (message instanceof Signal)
			handleMessage((Signal<?>) message);
		else if (message instanceof CloseEntityActor)
			handleMessage((CloseEntityActor) message);
	}

	private void handleMessage(CloseEntityActor message) {
		String key = getKey(message.getEntity());
		ActorRef actor = actors.remove(key);
		actor.tell(new StopEntityActor(), getSelf());
	}

	private String getKey(Entity<?> entity) {
		return entity.uniqueId();
	}

	private void handleMessage(EntityManagerFactory message) {
		emf = message;
	}

	private void handleMessage(Signal<?> message) {
		String key = getKey(message.getEntity());
		ActorRef actor = getActor(key);
		actor.tell(message, getSelf());
	}

	private ActorRef getActor(String key) {
		if (actors.get(key) == null) {
			ActorRef actor = createActor();
			actors.put(key, actor);
			actor.tell(emf, getSelf());
		}
		return actors.get(key);
	}

	private ActorRef createActor() {
		return getContext().actorOf(new Props(EntityActor.class));
	}
}
