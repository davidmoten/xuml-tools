package xuml.tools.model.compiler.runtime;

import java.util.Set;

import xuml.tools.model.compiler.runtime.SignalProcessorListener;
import xuml.tools.model.compiler.runtime.actor.EntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;

import com.google.common.collect.Sets;

public class SignalProcessorListenerStopsSignalProcessingSingleEntityOnFailure
		implements SignalProcessorListener {

	private static Set<String> stoppedEntities = Sets.newHashSet();

	@Override
	public synchronized void beforeProcessing(Signal<?> signal,
			EntityActor actor) {
		if (stoppedEntities.contains(signal.getEntity().uniqueId()))
			throw new SignalProcessingStoppedException(signal.getEntity()
					.uniqueId());
	}

	@Override
	public void afterProcessing(Signal<?> signal, EntityActor actor) {
		// do nothing
	}

	@Override
	public synchronized void failure(Signal<?> signal, Exception e,
			EntityActor actor) {
		if (!(e instanceof SignalProcessingStoppedException))
			stoppedEntities.add(signal.getEntity().uniqueId());
	}

	public static class SignalProcessingStoppedException extends
			RuntimeException {

		private static final long serialVersionUID = 4537196557689877257L;

		public SignalProcessingStoppedException(String message) {
			super(message);
		}

	}

}
