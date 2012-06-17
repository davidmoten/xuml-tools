package xuml.tools.jaxb.compiler;

public class SignalToOther<T extends Event<?>> {

	private final T event;
	private final Class<T> cls;

	public SignalToOther(Class<T> cls, T event) {
		this.cls = cls;
		this.event = event;
	}

	public T getEvent() {
		return event;
	}

	public Class<T> getClazz() {
		return cls;
	}

}