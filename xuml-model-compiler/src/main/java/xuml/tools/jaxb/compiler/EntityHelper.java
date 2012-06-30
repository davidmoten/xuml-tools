package xuml.tools.jaxb.compiler;

import java.util.List;
import java.util.Stack;

import xuml.tools.jaxb.compiler.actor.Info;
import xuml.tools.jaxb.compiler.actor.Signaller;
import xuml.tools.jaxb.compiler.message.Signal;

import com.google.common.collect.Lists;

/**
 * Each {@link Entity} has one instance of this.
 * 
 * @author dave
 * 
 */
public class EntityHelper {

	private final Entity entity;
	private final Stack<Call> stack = new Stack<Call>();
	private final List<Signal> signalsToOther = Lists.newArrayList();

	public EntityHelper(Entity entity) {
		this.entity = entity;
	}

	public void beforeEvent() {

		stack.push(new Call());
		Info info = Signaller.getInstance().getInfo();
		info.setCurrentEntity(entity);
	}

	public <T> void signal(Event<T> event) {
		Info info = Signaller.getInstance().getInfo();
		// do an object equals because RootActor will guarantee that only one
		// instance is being used to refer to a database entity at any given
		// time.
		boolean isSignalToSelf = entity == info.getCurrentEntity();
		if (isSignalToSelf)
			stack.peek().getEventsToSelf().add(event);
		else
			Signaller.getInstance().signal(entity, event);
	}

	public void queueSignal(Entity ent, Event event) {
		signalsToOther.add(new Signal(ent, event));
	}

	public void sendQueuedSignals() {
		for (Signal signal : signalsToOther) {
			Signaller.getInstance().signal(signal.getEntity(),
					signal.getEvent());
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
			Info info = Signaller.getInstance().getInfo();
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

}
