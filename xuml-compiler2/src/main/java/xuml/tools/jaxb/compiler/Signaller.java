package xuml.tools.jaxb.compiler;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;

import com.google.common.collect.Maps;

public class Signaller<T extends Entity<T, R>, R> {

	private final Class<T> cls;
	private final Map<R, ActorRef> actors = Maps.newHashMap();
	private final EntityManagerFactory emf;
	private static ActorSystem system = ActorSystem.create();

	public Signaller(EntityManagerFactory emf, Class<T> cls) {
		this.emf = emf;
		this.cls = cls;
	}

	/**
	 * Asynchronously signals an object (defined by the cls and id pair) with
	 * the given event. As per Mellow & Balcer 11.2.
	 * 
	 * @param cls
	 * @param id
	 * @param event
	 */
	public synchronized void signal(final R id, final Event<T> event) {
		if (actors.get(id) == null) {
			ActorRef actor = createActor(id, event);
			actors.put(id, actor);
		}
		actors.get(id).tell(event);
	}

	private ActorRef createActor(final R id, final Event<T> event) {
		return system.actorOf(new Props(new UntypedActorFactory() {
			private static final long serialVersionUID = 2036603134850456388L;

			@Override
			public Actor create() {
				return new EntityActor<T, R>(emf, cls, id);
			}
		}).withDispatcher("entity-dispatcher"));
	}
}
