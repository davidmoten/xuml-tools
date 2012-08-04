package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class DateExpressionField<T extends Entity<T>> extends DateExpression<T> {

	private final Field field;

	public DateExpressionField(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}

}
