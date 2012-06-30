package xuml.tools.jaxb.compiler.message;

public class Signal<T> {
	private final EntityEvent<T> entityEvent;

	public Signal(EntityEvent<T> entityEvent) {
		super();
		this.entityEvent = entityEvent;
	}

	public EntityEvent<T> getEntityEvent() {
		return entityEvent;
	}

}
