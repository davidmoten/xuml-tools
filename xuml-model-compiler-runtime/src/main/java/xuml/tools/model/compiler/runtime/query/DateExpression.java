package xuml.tools.model.compiler.runtime.query;

import java.util.Date;

import xuml.tools.model.compiler.runtime.Entity;

public abstract class DateExpression<T extends Entity<T>> {

    public BooleanExpression<T> lt(DateExpression<T> e) {
        return new DateComparison<T>(this, DateComparisonOperator.LT, e);
    }

    public BooleanExpression<T> gt(DateExpression<T> e) {
        return new DateComparison<T>(this, DateComparisonOperator.GT, e);
    }

    public BooleanExpression<T> before(DateExpression<T> e) {
        return new DateComparison<T>(this, DateComparisonOperator.LT, e);
    }

    public BooleanExpression<T> after(DateExpression<T> e) {
        return new DateComparison<T>(this, DateComparisonOperator.GT, e);
    }

    public BooleanExpression<T> lte(DateExpression<T> e) {
        return new DateComparison<T>(this, DateComparisonOperator.LTE, e);
    }

    public BooleanExpression<T> gte(DateExpression<T> e) {
        return new DateComparison<T>(this, DateComparisonOperator.GTE, e);
    }

    public BooleanExpression<T> eq(DateExpression<T> e) {
        return new DateComparison<T>(this, DateComparisonOperator.EQ, e);
    }

    public BooleanExpression<T> neq(DateExpression<T> e) {
        return new DateComparison<T>(this, DateComparisonOperator.NEQ, e);
    }

    public BooleanExpression<T> lt(Date e) {
        return new DateComparison<T>(this, DateComparisonOperator.LT, new DateConstant<T>(e));
    }

    public BooleanExpression<T> gt(Date e) {
        return new DateComparison<T>(this, DateComparisonOperator.GT, new DateConstant<T>(e));
    }

    public BooleanExpression<T> before(Date e) {
        return new DateComparison<T>(this, DateComparisonOperator.LT, new DateConstant<T>(e));
    }

    public BooleanExpression<T> after(Date e) {
        return new DateComparison<T>(this, DateComparisonOperator.GT, new DateConstant<T>(e));
    }

    public BooleanExpression<T> lte(Date e) {
        return new DateComparison<T>(this, DateComparisonOperator.LTE, new DateConstant<T>(e));
    }

    public BooleanExpression<T> gte(Date e) {
        return new DateComparison<T>(this, DateComparisonOperator.GTE, new DateConstant<T>(e));
    }

    public BooleanExpression<T> eq(Date e) {
        return new DateComparison<T>(this, DateComparisonOperator.EQ, new DateConstant<T>(e));
    }

    public BooleanExpression<T> neq(Date e) {
        return new DateComparison<T>(this, DateComparisonOperator.NEQ, new DateConstant<T>(e));
    }

}