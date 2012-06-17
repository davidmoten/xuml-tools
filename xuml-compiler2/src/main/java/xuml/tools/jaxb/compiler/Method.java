package xuml.tools.jaxb.compiler;

import java.util.List;

public class Method {

	private final String name;
	private final Type type;
	private final List<Parameter> parameters;

	public Method(String name, Type returnType, List<Parameter> parameters) {
		this.name = name;
		this.type = returnType;
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

}
