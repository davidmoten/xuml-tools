package xuml.tools.model.compiler.runtime;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import xuml.tools.model.compiler.runtime.actor.EntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;

public class SignalProcessorListenerTesting implements SignalProcessorListener {

    private final List<Exception> exceptions = new CopyOnWriteArrayList<>();

    private static Logger log = Logger.getLogger(SignalProcessorListenerTesting.class.getName());

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
        log.log(Level.SEVERE, e.getMessage(), e);
        exceptions.add(e);
    }

    public List<Exception> exceptions() {
        return exceptions;
    }

}
