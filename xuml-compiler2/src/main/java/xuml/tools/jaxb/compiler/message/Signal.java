package xuml.tools.jaxb.compiler.message;

public class Signal<T> {
	private final EntityEvent<T> entityEvent;
	private final boolean toSelf;

	public Signal(EntityEvent<T> entityEvent, boolean toSelf) {
		super();
		this.entityEvent = entityEvent;
		this.toSelf = toSelf;
	}

	public EntityEvent<T> getEntityEvent() {
		return entityEvent;
	}

	public boolean toSelf() {
		return toSelf;
	}
}
