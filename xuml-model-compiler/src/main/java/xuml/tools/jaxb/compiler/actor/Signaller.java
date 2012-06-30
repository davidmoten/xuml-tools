package xuml.tools.jaxb.compiler.actor;

import javax.persistence.EntityManagerFactory;

import xuml.tools.jaxb.compiler.Entity;
import xuml.tools.jaxb.compiler.Event;
import xuml.tools.jaxb.compiler.message.EntityCommit;
import xuml.tools.jaxb.compiler.message.Signal;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Signaller {

	private final ThreadLocal<Info> info = new ThreadLocal<Info>() {
		@Override
		protected Info initialValue() {
			return new Info();
		}
	};
	private final ActorSystem actorSystem = ActorSystem.create();
	private final ActorRef root = actorSystem.actorOf(
			new Props(RootActor.class), "root");

	private static Signaller instance;

	public static Signaller getInstance() {
		if (null == instance) {
			instance = new Signaller();
		}
		return instance;
	}

	private Signaller() {
	}

	/**
	 * Sets the entity manager factory for all database interaction managed by
	 * the signaller.
	 * 
	 * @param emf
	 */
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		root.tell(emf);
	}

	public <T, R> void signal(Entity<T> entity, Event<T> event) {
		if (signalInitiatedFromEvent()) {
			info.get().getCurrentEntity().entityHelper()
					.queueSignal(entity, event);
		} else {
			root.tell(new Signal<T>(entity, event));
		}
	}

	private boolean signalInitiatedFromEvent() {
		return info.get().getCurrentEntity() != null;
	}

	public <T, R> void signalCommit(Entity<T> entity) {
		root.tell(new EntityCommit<T>(entity));
	}

	public Info getInfo() {
		return info.get();
	}
}
