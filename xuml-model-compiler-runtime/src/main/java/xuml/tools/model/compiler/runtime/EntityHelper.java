package xuml.tools.model.compiler.runtime;

import java.util.List;
import java.util.Stack;

import javax.persistence.EntityManager;

import scala.concurrent.duration.Duration;
import xuml.tools.model.compiler.runtime.message.Signal;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * Each {@link Entity} has one instance of this.
 * 
 * @author dave
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EntityHelper {

	private final Entity entity;
	private final Stack<Call> stack = new Stack<Call>();
	private final List<Signal> signalsToOther = Lists.newArrayList();
	private final Signaller signaller;

	public EntityHelper(Signaller signaller, Entity entity) {
		this.signaller = signaller;
		this.entity = entity;
	}

	public void beforeEvent() {

		stack.push(new Call());
		Info info = signaller.getInfo();
		info.setCurrentEntity(entity);
	}

	public <T extends Entity<T>> void signal(Event<T> event) {
		signal(event, Optional.<Duration>absent());
	}

	public <T extends Entity<T>> void signal(Event<T> event, Optional<Duration> delay) {
		Info info = signaller.getInfo();
		// do an object equals because RootActor will guarantee that only one
		// instance is being used to refer to a database entity at any given
		// time.
		boolean isSignalToSelf = entity == info.getCurrentEntity();
		if (isSignalToSelf)
			// delay is ignored signals to self
			stack.peek().getEventsToSelf().add(event);
		else {
			String fromEntityUniqueId;
			if (info.getCurrentEntity() == null)
				fromEntityUniqueId = "Unknown";
			else
				fromEntityUniqueId = info.getCurrentEntity().uniqueId();
			signaller.signal(fromEntityUniqueId, entity, event, delay);
		}
	}

	public <T> void queueSignal(Signal<T> signal) {
		signalsToOther.add(signal);
	}

	public void sendQueuedSignals() {
		for (Signal signal : signalsToOther) {
			signaller.signal(signal.getFromEntityUniqueId(),
					signal.getEntity(), signal.getEvent(), signal.getTime(),
					signal.getRepeatInterval());
		}
	}

	/**
	 * Just after each Entity.performEvent is called we perform the events to
	 * self that were called during that event.
	 */
	public void afterEvent() {
		Call call = stack.peek();
		for (Event event : call.getEventsToSelf()) {
			entity.event(event);
		}
		stack.pop();
		if (stack.size() == 0) {
			Info info = signaller.getInfo();
			// reset the thread local variable so that the next use of this
			// thread will not make an assumption about the current entity
			info.setCurrentEntity(null);
		}

	}

	private static class Call {
		private final List<Event> eventsToSelf = Lists.newArrayList();

		public List<Event> getEventsToSelf() {
			return eventsToSelf;
		}
	}

	public void setEntityManager(EntityManager em) {
		signaller.getInfo().setCurrentEntityManager(em);
	}

	public EntityManager getEntityManager() {
		return signaller.getInfo().getCurrentEntityManager();
	}

}
