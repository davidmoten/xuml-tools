package xuml.tools.model.compiler.runtime.query;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.persistence.EntityManager;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.Info;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class SelectBuilder<T extends Entity<T>> {

	private final BooleanExpression<T> e;
	private Info info;
	private Class<T> entityClass;

	public SelectBuilder(BooleanExpression<T> e) {
		this.e = e;
	}

	public static <R extends Entity<R>> SelectBuilder<R> builder(
			BooleanExpression<R> e) {
		return new SelectBuilder<R>(e);
	}

	public SelectBuilder<T> select(BooleanExpression<T> exp) {
		return new SelectBuilder<T>(e.and(exp));
	}

	public SelectBuilder<T> entityClass(Class<T> cls) {
		entityClass = cls;
		return this;
	}

	public SelectBuilder<T> info(Info info) {
		Preconditions
				.checkNotNull(
						info,
						"thread local Info not available. You need to set the Context EntityManagerFactory before create a SelectBuilder");
		this.info = info;
		return this;
	}

	public List<T> many() {
		return many(info.getCurrentEntityManager());
	}

	public T one() {
		return one(info.getCurrentEntityManager());
	}

	public List<T> many(EntityManager em) {
		Preconditions.checkNotNull(em, "entity manager is null!");
		Preconditions.checkNotNull(e, "BooleanExpression cannot be null");
		String clause = getClause();
		// TODO use clause
		return Lists.newArrayList();
	}

	@VisibleForTesting
	String getClause() {
		return getClause(e);
	}

	private String getClause(BooleanExpression<T> e) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		if (e instanceof Not) {
			Not<T> not = (Not<T>) e;
			out.print("!(" + getClause(not.getExpression()) + ")");
		} else if (e instanceof NumericComparison) {
			NumericComparison<T> c = (NumericComparison<T>) e;
			String op = getOperator(c.getOperator());
			out.print("(" + getClause(c.getExpression1()) + op
					+ getClause(c.getExpression2()) + ")");
		} else if (e instanceof BinaryBooleanExpression) {
			BinaryBooleanExpression<T> b = (BinaryBooleanExpression<T>) e;
			out.print("(" + getClause(b.getExpression1()) + " "
					+ getOperator(b.getOperator()) + " "
					+ getClause(b.getExpression2()) + ")");

		}
		out.close();
		return bytes.toString();
	}

	private String getOperator(BinaryBooleanOperator op) {
		if (op == BinaryBooleanOperator.AND)
			return "and";
		else if (op == BinaryBooleanOperator.OR)
			return "or";
		else
			throw new RuntimeException("not implemented " + op);
	}

	private String getOperator(NumericComparisonOperator c) {
		String op;
		if (c == NumericComparisonOperator.EQ)
			op = "=";
		else if (c == NumericComparisonOperator.NEQ)
			op = "!=";
		else if (c == NumericComparisonOperator.LT)
			op = "<";
		else if (c == NumericComparisonOperator.GT)
			op = ">";
		else if (c == NumericComparisonOperator.LTE)
			op = "<=";
		else if (c == NumericComparisonOperator.GTE)
			op = ">=";
		else
			throw new RuntimeException("unimplemented operator " + c);
		return op;
	}

	private String getClause(NumericExpression<T> e) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		if (e instanceof BinaryNumericExpression) {
			BinaryNumericExpression<T> b = (BinaryNumericExpression<T>) e;
			String op = getOperator(b.getOperator());
			out.print("(" + getClause(b.getExpression1()) + op
					+ getClause(b.getExpression2()) + ")");
		} else if (e instanceof NumericConstant) {
			NumericConstant<T> c = (NumericConstant<T>) e;
			out.print(c.getValue());
		} else if (e instanceof NumericExpressionField) {
			NumericExpressionField<T> f = (NumericExpressionField<T>) e;
			out.print(f.getField().getName());
		}
		out.close();
		return bytes.toString();
	}

	private String getOperator(BinaryNumericOperator op) {

		if (op == BinaryNumericOperator.DIVIDE)
			return "/";
		else if (op == BinaryNumericOperator.MINUS)
			return "-";
		else if (op == BinaryNumericOperator.PLUS)
			return "+";
		else if (op == BinaryNumericOperator.TIMES)
			return "*";
		else
			throw new RuntimeException("not implemented " + op);
	}

	public T one(EntityManager em) {
		List<T> list = many();
		if (list.size() == 1)
			return list.get(0);
		else
			throw new RuntimeException("returned " + list.size()
					+ " results and only one was expected");
	}

}
