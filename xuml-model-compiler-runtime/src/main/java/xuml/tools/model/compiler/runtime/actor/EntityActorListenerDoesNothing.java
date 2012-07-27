package xuml.tools.model.compiler.runtime.actor;

import xuml.tools.model.compiler.runtime.message.Signal;

public class EntityActorListenerDoesNothing implements EntityActorListener {

	private static EntityActorListenerDoesNothing instance;

	public static synchronized EntityActorListenerDoesNothing getInstance() {
		if (instance == null)
			instance = new EntityActorListenerDoesNothing();
		return instance;
	}

	@Override
	public void beforeProcessing(Signal<?> signal, EntityActor actor) {
		// does nothing
	}

	@Override
	public void afterProcessing(Signal<?> signal, EntityActor actor) {
		// does nothing
	}

	@Override
	public void failure(Signal<?> signal, Exception ex, EntityActor actor) {
		// does nothing
	}

}
