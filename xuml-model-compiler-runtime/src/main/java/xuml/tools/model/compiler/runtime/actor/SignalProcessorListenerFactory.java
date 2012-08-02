package xuml.tools.model.compiler.runtime.actor;

public interface SignalProcessorListenerFactory {

	SignalProcessorListener create(String entityUniqueId);
}
