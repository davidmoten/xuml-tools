package xuml.tools.model.compiler.runtime.query;

import java.time.Duration;
import java.util.Date;

import xuml.tools.model.compiler.runtime.Entity;

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
