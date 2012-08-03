package xuml.tools.model.compiler.runtime.query;

import java.util.Date;

import xuml.tools.model.compiler.runtime.Entity;
import akka.util.Duration;

public class DateConstant<T extends Entity<T>> extends DateExpression<T> {

	private final Date value;

	public DateConstant(Date value) {
		this.value = value;
	}

	public DateConstant(Duration duration) {
		this(new Date(duration.toMillis()));
	}

	public Date getValue() {
		return value;
	}

}
