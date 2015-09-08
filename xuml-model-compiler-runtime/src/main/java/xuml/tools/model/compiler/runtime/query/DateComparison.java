package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class DateComparison<T extends Entity<T>> extends BooleanExpression<T> {
    private final DateExpression<T> n1;
    private final DateComparisonOperator op;
    private final DateExpression<T> n2;

    public DateComparison(DateExpression<T> n1, DateComparisonOperator op, DateExpression<T> n2) {
        super();
        this.n1 = n1;
        this.op = op;
        this.n2 = n2;
    }

    public DateExpression<T> getExpression1() {
        return n1;
    }

    public DateComparisonOperator getOperator() {
        return op;
    }

    public DateExpression<T> getExpression2() {
        return n2;
    }
}
