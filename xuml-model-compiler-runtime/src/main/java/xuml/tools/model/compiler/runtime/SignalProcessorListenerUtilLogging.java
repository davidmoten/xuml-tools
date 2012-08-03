package xuml.tools.model.compiler.runtime;

import java.util.logging.Level;
import java.util.logging.Logger;

import xuml.tools.model.compiler.runtime.actor.EntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;

public class SignalProcessorListenerUtilLogging implements
		SignalProcessorListener {

	private static Logger log = Logger
			.getLogger(SignalProcessorListenerUtilLogging.class.getName());

	@Override
	public void beforeProcessing(Signal<?> signal, EntityActor actor) {
		log.fine("before processing " + signal);
	}

	@Override
	public void afterProcessing(Signal<?> signal, EntityActor actor) {
		log.fine("after processing " + signal);
	}

	@Override
	public void failure(Signal<?> signal, Exception e, EntityActor actor) {
		// log the failure
		String msg = "error processing " + signal + ": " + e.getMessage();
		log.log(Level.SEVERE, msg, e);
	}

}
