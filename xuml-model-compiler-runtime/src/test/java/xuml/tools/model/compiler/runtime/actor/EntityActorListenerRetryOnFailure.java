package xuml.tools.model.compiler.runtime.actor;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import xuml.tools.model.compiler.runtime.message.Signal;
import akka.util.Duration;

public class EntityActorListenerRetryOnFailure implements SignalProcessorListener {

	private static Logger log = Logger
			.getLogger(EntityActorListenerRetryOnFailure.class.getName());

	private static final Duration DELAY = Duration.create(5, TimeUnit.MINUTES);

	@Override
	public void beforeProcessing(Signal<?> signal, EntityActor actor) {
		// do nothing
	}

	@Override
	public void afterProcessing(Signal<?> signal, EntityActor actor) {
		// do nothing
	}

	@Override
	public void failure(Signal<?> signal, Exception e, EntityActor actor) {
		// log the failure
		String msg = "error processing " + signal + ": " + e.getMessage();
		log.log(Level.SEVERE, msg, e);
		// retry the message after an interval
		resend(signal, DELAY);
	}

	private <T> void resend(Signal<T> signal, Duration delay) {
		signal.getEntity().signal(signal.getEvent(), DELAY);
	}

}
