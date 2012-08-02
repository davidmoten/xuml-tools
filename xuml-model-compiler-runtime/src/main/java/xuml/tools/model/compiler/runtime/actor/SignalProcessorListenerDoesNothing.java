package xuml.tools.model.compiler.runtime.actor;

import xuml.tools.model.compiler.runtime.message.Signal;

public class SignalProcessorListenerDoesNothing implements SignalProcessorListener {

	private static SignalProcessorListenerDoesNothing instance;

	public static synchronized SignalProcessorListenerDoesNothing getInstance() {
		if (instance == null)
			instance = new SignalProcessorListenerDoesNothing();
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
