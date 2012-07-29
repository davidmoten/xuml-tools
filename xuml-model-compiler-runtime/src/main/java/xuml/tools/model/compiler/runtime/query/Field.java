package xuml.tools.model.compiler.runtime.query;


public class Field {

	private final String fieldName;

	public Field(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getValue() {
		return fieldName;
	}

}
