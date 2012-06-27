package xuml.tools.jaxb.compiler.actor;

import xuml.tools.jaxb.compiler.Entity;
import xuml.tools.jaxb.compiler.Event;
import xuml.tools.jaxb.compiler.message.EntityEvent;
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

	public <T, R> void signal(Entity<T, R> entity, Event<T> event) {
		boolean signalToSelf = entity.equals(info.get().getCurrentEntity());
		EntityEvent<T, R> ee = new EntityEvent<T, R>(entity, event);
		root.tell(new Signal<T, R>(ee, signalToSelf));
	}
}
