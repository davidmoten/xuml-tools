package xuml.tools.model.compiler.runtime.actor;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.message.Signal;

public class EntityActorListenerDoesNothing implements EntityActorListener {

	private static EntityActorListenerDoesNothing instance;

	public static synchronized EntityActorListenerDoesNothing getInstance() {
		if (instance == null)
			instance = new EntityActorListenerDoesNothing();
		return instance;
	}

	@Override
	public void beforeProcessing(Entity<?> entity, Signal<?> signal) {
		// does nothing
	}

	@Override
	public void afterProcessing(Entity<?> entity, Signal<?> signal) {
		// does nothing
	}

	@Override
	public void failure(Entity<?> entity, Signal<?> signal, Exception ex) {
		// does nothing
	}

}
