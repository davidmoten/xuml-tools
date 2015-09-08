package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public abstract class StringExpression<T extends Entity<T>> {

    public BooleanExpression<T> eq(StringExpression<T> e) {
        return new StringComparison<T>(this, StringComparisonOperator.EQ, e);
    }

    public BooleanExpression<T> eq(String s) {
        return new StringComparison<T>(this, StringComparisonOperator.EQ, new StringConstant<T>(s));
    }

    public BooleanExpression<T> neq(StringExpression<T> e) {
        return new StringComparison<T>(this, StringComparisonOperator.NEQ, e);
    }

    public BooleanExpression<T> neq(String s) {
        return new StringComparison<T>(this, StringComparisonOperator.NEQ,
                new StringConstant<T>(s));
    }

    public BooleanExpression<T> gt(String s) {
        return new StringComparison<T>(this, StringComparisonOperator.GT, new StringConstant<T>(s));
    }

    public BooleanExpression<T> gte(String s) {
        return new StringComparison<T>(this, StringComparisonOperator.GTE,
                new StringConstant<T>(s));
    }

    public BooleanExpression<T> lt(String s) {
        return new StringComparison<T>(this, StringComparisonOperator.LT, new StringConstant<T>(s));
    }

    public BooleanExpression<T> lte(String s) {
        return new StringComparison<T>(this, StringComparisonOperator.LTE,
                new StringConstant<T>(s));
    }

    public BooleanExpression<T> like(String s) {
        return new StringComparison<T>(this, StringComparisonOperator.LIKE,
                new StringConstant<T>(s));
    }
}
