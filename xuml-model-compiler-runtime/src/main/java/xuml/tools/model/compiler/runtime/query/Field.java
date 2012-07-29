package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class Field<T extends Entity<T>> {

	private final String fieldName;

	public Field(String fieldName, Class<T> cls) {
		this.fieldName = fieldName;
	}

	public String getValue() {
		return fieldName;
	}

}
