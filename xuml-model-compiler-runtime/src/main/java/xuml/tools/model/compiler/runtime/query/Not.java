package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class Not<T extends Entity<T>> extends BooleanExpression<T> {

	private final BooleanExpression<T> e;

	public Not(BooleanExpression<T> e) {
		this.e = e;
	}

	public BooleanExpression<T> getExpression() {
		return e;
	}
}
