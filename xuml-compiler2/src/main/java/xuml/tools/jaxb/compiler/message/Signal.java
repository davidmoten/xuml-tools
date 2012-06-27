package xuml.tools.jaxb.compiler.message;


public class Signal<T, R> {
	private final EntityEvent<T, R> entityEvent;
	private final boolean toSelf;

	public Signal(EntityEvent<T, R> entityEvent, boolean toSelf) {
		super();
		this.entityEvent = entityEvent;
		this.toSelf = toSelf;
	}

	public EntityEvent<T, R> getEntityEvent() {
		return entityEvent;
	}

	public boolean toSelf() {
		return toSelf;
	}
}
