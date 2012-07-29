package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class StringExpressionField<T extends Entity<T>> extends
		StringExpression<T> {

	private final Field<T> field;

	public StringExpressionField(Field<T> field, Class<T> cls) {
		super();
		this.field = field;
	}

	public Field<T> getField() {
		return field;
	}

}
