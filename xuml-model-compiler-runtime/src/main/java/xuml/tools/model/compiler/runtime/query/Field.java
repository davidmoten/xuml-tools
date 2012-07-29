package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class Field<T extends Entity<T>, R extends ExpressionType> {

	private final String fieldName;

	public Field(String fieldName, Class<R> expressionType) {
		this.fieldName = fieldName;
	}

	public String getValue() {
		return fieldName;
	}

}
