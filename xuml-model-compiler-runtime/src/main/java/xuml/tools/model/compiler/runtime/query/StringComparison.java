package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class StringComparison<T extends Entity<T>> extends BooleanExpression<T> {

    private final StringExpression<T> e1;
    private final StringExpression<T> e2;
    private final StringComparisonOperator op;

    public StringComparison(StringExpression<T> e1, StringComparisonOperator op,
            StringExpression<T> e2) {
        this.e1 = e1;
        this.op = op;
        this.e2 = e2;
    }

    public StringExpression<T> getExpression1() {
        return e1;
    }

    public StringExpression<T> getExpression2() {
        return e2;
    }

    public StringComparisonOperator getOperator() {
        return op;
    }
}
