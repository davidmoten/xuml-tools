package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class CriteriaBuilder<T extends Entity<T>> {

	private final BooleanExpression<T> e;

	private CriteriaBuilder(BooleanExpression<T> e) {
		this.e = e;
	}

	public CriteriaBuilder<T> and(BooleanExpression<T> exp) {
		return new CriteriaBuilder<T>(e.and(exp));
	}

	public CriteriaBuilder<T> or(BooleanExpression<T> exp) {
		return new CriteriaBuilder<T>(e.or(exp));
	}

}
