package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;
import akka.util.Duration;

public abstract class DateExpression<T extends Entity<T>> {

	public BooleanExpression<T> before(DateExpression<T> e) {
		return new DateComparison<T>(this, DateComparisonOperator.BEFORE, e);
	}

	public BooleanExpression<T> after(DateExpression<T> e) {
		return new DateComparison<T>(this, DateComparisonOperator.AFTER, e);
	}

	public DateExpression<T> add(Duration duration) {
		return new BinaryDateExpression<T>(this, BinaryDateOperator.ADD,
				duration);
	}

}