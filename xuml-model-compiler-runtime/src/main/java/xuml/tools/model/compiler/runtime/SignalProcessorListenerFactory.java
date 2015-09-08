package xuml.tools.model.compiler.runtime;

public interface SignalProcessorListenerFactory {

    SignalProcessorListener create(String entityUniqueId);
}
