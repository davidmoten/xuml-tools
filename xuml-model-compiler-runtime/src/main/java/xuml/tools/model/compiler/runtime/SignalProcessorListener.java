package xuml.tools.model.compiler.runtime;

import xuml.tools.model.compiler.runtime.actor.EntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;

public interface SignalProcessorListener {

	void beforeProcessing(Signal<?> signal, EntityActor actor);

	void afterProcessing(Signal<?> signal, EntityActor actor);

	void failure(Signal<?> signal, Exception e, EntityActor actor);

}
