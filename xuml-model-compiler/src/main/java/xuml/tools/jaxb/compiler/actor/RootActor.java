package xuml.tools.jaxb.compiler.actor;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;

import xuml.tools.jaxb.compiler.Entity;
import xuml.tools.jaxb.compiler.message.CloseEntityActor;
import xuml.tools.jaxb.compiler.message.HasEntity;
import xuml.tools.jaxb.compiler.message.StopEntityActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

import com.google.common.collect.Maps;

public class RootActor extends UntypedActor {

	private EntityManagerFactory emf;
	private final HashMap<String, ActorRef> actors = Maps.newHashMap();

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof EntityManagerFactory)
			handleMessage((EntityManagerFactory) message);
		else if (message instanceof HasEntity)
			handleMessage((HasEntity<?>) message);
		else if (message instanceof CloseEntityActor)
			handleMessage((CloseEntityActor) message);
	}

	private void handleMessage(CloseEntityActor message) {
		String key = getKey(message.getEntity());
		ActorRef actor = actors.remove(key);
		actor.tell(new StopEntityActor());
	}

	private String getKey(Entity<?> entity) {
		return entity.uniqueId();
	}

	private void handleMessage(EntityManagerFactory message) {
		emf = message;
	}

	private void handleMessage(HasEntity<?> message) {
		String key = getKey(message.getEntity());
		ActorRef actor = getActor(key);
		actor.tell(message, getSelf());
	}

	private ActorRef getActor(String key) {
		if (actors.get(key) == null) {
			ActorRef actor = createActor();
			actors.put(key, actor);
			actor.tell(emf);
		}
		return actors.get(key);
	}

	private ActorRef createActor() {
		return getContext().actorOf(new Props(new UntypedActorFactory() {
			private static final long serialVersionUID = 2036603134850456388L;

			@Override
			public Actor create() {
				return new EntityActor();
			}
			// }).withDispatcher("entity-dispatcher"));
		}));
	}
}
