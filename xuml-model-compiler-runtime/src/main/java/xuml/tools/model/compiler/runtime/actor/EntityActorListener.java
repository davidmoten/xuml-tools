package xuml.tools.model.compiler.runtime.actor;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.message.Signal;

public interface EntityActorListener {

	void beforeProcessing(Entity<?> entity, Signal<?> signal);

	void afterProcessing(Entity<?> entity, Signal<?> signal);

	void failure(Entity<?> entity, Signal<?> signal, Exception e);

}
