package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class NumericExpressionField<T extends Entity<T>> extends
		NumericExpression<T> {

	private final Field<T> field;

	public NumericExpressionField(Field<T> field, Class<T> cls) {
		this.field = field;
	}

	public Field<T> getField() {
		return field;
	}

}
