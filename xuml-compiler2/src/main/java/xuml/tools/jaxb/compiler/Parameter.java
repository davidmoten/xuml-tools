package xuml.tools.jaxb.compiler;

public class Parameter {
	private final String name;
	private final Type type;

	public Parameter(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

}
