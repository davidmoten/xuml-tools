package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class IsNullString<T extends Entity<T>> extends StringExpression<T> {

	private final StringExpression<T> e;

	public IsNullString(StringExpression<T> e) {
		this.e = e;
	}

	public StringExpression<T> getExpression() {
		return e;
	}
}
