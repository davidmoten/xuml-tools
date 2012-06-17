package xuml.tools.jaxb.compiler;

public class SignalToSelf<T extends Event<?>> {

	private final T t;
	private final Class<T> cls;

	public SignalToSelf(Class<T> cls, T t) {
		this.cls = cls;
		this.t = t;
	}

	public T getValue() {
		return t;
	}

	public Class<T> getClazz() {
		return cls;
	}

}
