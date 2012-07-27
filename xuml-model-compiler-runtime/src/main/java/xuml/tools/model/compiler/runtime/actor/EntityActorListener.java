package xuml.tools.model.compiler.runtime.actor;

import xuml.tools.model.compiler.runtime.message.Signal;

public interface EntityActorListener {

	void beforeProcessing(Signal<?> signal, EntityActor actor);

	void afterProcessing(Signal<?> signal, EntityActor actor);

	void failure(Signal<?> signal, Exception e, EntityActor actor);

}
