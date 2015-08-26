package xuml.tools.model.compiler.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xuml.tools.model.compiler.runtime.actor.EntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;

public class SignalProcessorListenerSlf4j implements SignalProcessorListener {

    private static Logger log = LoggerFactory.getLogger(SignalProcessorListenerSlf4j.class);

    @Override
    public void beforeProcessing(Signal<?> signal, EntityActor actor) {
        log.debug("before processing " + signal);
    }

    @Override
    public void afterProcessing(Signal<?> signal, EntityActor actor) {
        log.debug("after processing " + signal);
    }

    @Override
    public void failure(Signal<?> signal, Exception e, EntityActor actor) {
        // log the failure
        String msg = "error processing " + signal + ": " + e.getMessage();
        log.error(msg, e);
    }

}
