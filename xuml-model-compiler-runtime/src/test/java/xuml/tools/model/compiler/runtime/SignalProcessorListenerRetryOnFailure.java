package xuml.tools.model.compiler.runtime;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import scala.concurrent.duration.Duration;
import xuml.tools.model.compiler.runtime.actor.EntityActor;
import xuml.tools.model.compiler.runtime.message.Signal;

public class SignalProcessorListenerRetryOnFailure implements SignalProcessorListener {

    private static Logger log = Logger
            .getLogger(SignalProcessorListenerRetryOnFailure.class.getName());

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
        throw new RuntimeException("TODO");
        // signal.getEntity().signal(signal.getEvent(), DELAY);
    }

}
