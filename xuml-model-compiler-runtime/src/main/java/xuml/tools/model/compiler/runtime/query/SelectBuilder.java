package xuml.tools.model.compiler.runtime.query;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.Info;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class SelectBuilder<T extends Entity<T>> {

	private final BooleanExpression<T> e;
	private Info info;
	private Class<T> entityClass;
	private final AtomicInteger parameterNo = new AtomicInteger(0);

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

	private static class ClauseAndParameters {
		String clause;
		Map<String, Object> parameters = Maps.newHashMap();

		ClauseAndParameters(String clause, Map<String, Object> parameters) {
			super();
			this.clause = clause;
			this.parameters = parameters;
		}
	}

	public List<T> many(EntityManager em) {
		Preconditions.checkNotNull(em, "entity manager is null!");
		ClauseAndParameters c = getClauseAndParameters();
		String sql = getSql(entityClass, c.clause);
		System.out.println(sql);
		TypedQuery<T> query = em.createQuery(sql, entityClass);
		for (Entry<String, Object> p : c.parameters.entrySet())
			query.setParameter(p.getKey(), p.getValue());
		return query.getResultList();
	}

	private ClauseAndParameters getClauseAndParameters() {
		return getClauseAndParameters(e);
	}

	@VisibleForTesting
	static String getSql(Class<?> entityClass, String clause) {
		String prefix = "select e from " + entityClass.getSimpleName() + " e";
		String sql;
		if (clause.length() > 0)
			sql = prefix + " where " + clause;
		else
			sql = prefix;
		return sql;
	}

	@VisibleForTesting
	String getClause() {
		return getClauseAndParameters(e).clause;
	}

	private ClauseAndParameters getClauseAndParameters(BooleanExpression<T> e) {
		Map<String, Object> parameters = Maps.newHashMap();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		if (e instanceof Not) {
			Not<T> not = (Not<T>) e;
			ClauseAndParameters c = getClauseAndParameters(not.getExpression());
			parameters.putAll(c.parameters);
			out.print("!(" + c.clause + ")");
		} else if (e instanceof NumericComparison) {
			NumericComparison<T> c = (NumericComparison<T>) e;
			ClauseAndParameters c1 = getClauseAndParameters(c.getExpression1());
			ClauseAndParameters c2 = getClauseAndParameters(c.getExpression2());
			parameters.putAll(c1.parameters);
			parameters.putAll(c2.parameters);
			out.print("(" + c1.clause + " " + getOperator(c.getOperator())
					+ " " + c2.clause + ")");
		} else if (e instanceof BinaryBooleanExpression) {
			BinaryBooleanExpression<T> c = (BinaryBooleanExpression<T>) e;
			ClauseAndParameters c1 = getClauseAndParameters(c.getExpression1());
			ClauseAndParameters c2 = getClauseAndParameters(c.getExpression2());
			parameters.putAll(c1.parameters);
			parameters.putAll(c2.parameters);
			out.print("(" + c1.clause + " " + getOperator(c.getOperator())
					+ " " + c2.clause + ")");
		} else if (e instanceof StringComparison) {
			StringComparison<T> c = (StringComparison<T>) e;
			ClauseAndParameters c1 = getClauseAndParameters(c.getExpression1());
			ClauseAndParameters c2 = getClauseAndParameters(c.getExpression2());
			parameters.putAll(c1.parameters);
			parameters.putAll(c2.parameters);
			out.print("(" + c1.clause + " " + getOperator(c.getOperator())
					+ " " + c2.clause + ")");
		}
		out.close();
		return new ClauseAndParameters(bytes.toString(), parameters);
	}

	private String getOperator(StringComparisonOperator op) {
		if (op == StringComparisonOperator.EQ)
			return "=";
		else
			throw new RuntimeException("not implemented " + op);
	}

	private ClauseAndParameters getClauseAndParameters(StringExpression<T> e) {
		Map<String, Object> parameters = Maps.newHashMap();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		if (e instanceof BinaryStringExpression) {
			BinaryStringExpression<T> c = (BinaryStringExpression<T>) e;
			ClauseAndParameters c1 = getClauseAndParameters(c.getExpression1());
			ClauseAndParameters c2 = getClauseAndParameters(c.getExpression2());
			parameters.putAll(c1.parameters);
			parameters.putAll(c2.parameters);
			out.print("(" + c1.clause + " " + getOperator(c.getOperator())
					+ " " + c2.clause + ")");
		} else if (e instanceof StringConstant) {
			StringConstant<T> c = (StringConstant<T>) e;
			addToParameters(parameters, out, c.getValue());
		} else if (e instanceof StringExpressionField) {
			StringExpressionField<T> f = (StringExpressionField<T>) e;
			out.print(f.getField().getName());
		}
		out.close();
		return new ClauseAndParameters(bytes.toString(), parameters);
	}

	private void addToParameters(Map<String, Object> parameters,
			PrintStream out, Object object) {
		int index = parameterNo.incrementAndGet();
		String parameterName = "_p" + index;
		parameters.put(parameterName, object);
		out.print(":" + parameterName);
	}

	private String getOperator(BinaryStringOperator op) {
		if (op == BinaryStringOperator.PLUS)
			return "+";
		else
			throw new RuntimeException("not implemented " + op);
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

	private ClauseAndParameters getClauseAndParameters(NumericExpression<T> e) {
		Map<String, Object> parameters = Maps.newHashMap();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		if (e instanceof BinaryNumericExpression) {
			BinaryNumericExpression<T> c = (BinaryNumericExpression<T>) e;
			ClauseAndParameters c1 = getClauseAndParameters(c.getExpression1());
			ClauseAndParameters c2 = getClauseAndParameters(c.getExpression2());
			parameters.putAll(c1.parameters);
			parameters.putAll(c2.parameters);
			out.print("(" + c1.clause + " " + getOperator(c.getOperator())
					+ " " + c2.clause + ")");
		} else if (e instanceof NumericConstant) {
			NumericConstant<T> c = (NumericConstant<T>) e;
			addToParameters(parameters, out, c.getValue());
		} else if (e instanceof NumericExpressionField) {
			NumericExpressionField<T> f = (NumericExpressionField<T>) e;
			out.print(f.getField().getName());
		}
		out.close();
		return new ClauseAndParameters(bytes.toString(), parameters);
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
