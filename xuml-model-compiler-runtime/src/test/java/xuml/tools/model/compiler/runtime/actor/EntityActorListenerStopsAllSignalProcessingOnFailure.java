package xuml.tools.model.compiler.runtime.actor;

import xuml.tools.model.compiler.runtime.message.Signal;

public class EntityActorListenerStopsAllSignalProcessingOnFailure implements
		SignalProcessorListener {

	private static boolean failureOccured = false;

	@Override
	public void beforeProcessing(Signal<?> signal, EntityActor actor) {
		if (failureOccured)
			// Note that this throw will also prompt a call to the failure
			// method below.
			throw new FailedProcessingException(
					"failure has occurred, no further processing");
	}

	@Override
	public void afterProcessing(Signal<?> signal, EntityActor actor) {
		// do nothing
	}

	@Override
	public void failure(Signal<?> signal, Exception e, EntityActor actor) {
		if (!(e instanceof FailedProcessingException))
			failureOccured = true;
	}

	public static class FailedProcessingException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public FailedProcessingException(String message) {
			super(message);
		}

	}

}
