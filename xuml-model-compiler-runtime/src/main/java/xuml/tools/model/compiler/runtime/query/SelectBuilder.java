package xuml.tools.model.compiler.runtime.query;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.persistence.EntityManager;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.Info;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class SelectBuilder<T extends Entity<T>> {

	private final BooleanExpression<T> e;
	private Info info;
	private Class<T> entityClass;

	public SelectBuilder(BooleanExpression<T> e) {
		this.e = e;
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
		String clause = getWhereClause(e);
		return Lists.newArrayList();
	}

	private String getWhereClause(BooleanExpression<T> e) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		if (e instanceof Not) {
			Not<T> not = (Not<T>) e;
			out.print("!(" + getWhereClause(not.getExpression()) + ")");
		} else if (e instanceof NumericComparison) {
			NumericComparison<T> c = (NumericComparison<T>) e;
			String op;
			if (c.getOperator() == NumericComparisonOperator.EQ)
				op = "=";
			else if (c.getOperator() == NumericComparisonOperator.NEQ)
				op = "!=";
			else if (c.getOperator() == NumericComparisonOperator.LT)
				op = "<";
			else if (c.getOperator() == NumericComparisonOperator.GT)
				op = ">";
			else if (c.getOperator() == NumericComparisonOperator.LTE)
				op = "<=";
			else if (c.getOperator() == NumericComparisonOperator.GTE)
				op = ">=";
			else
				throw new RuntimeException("unimplemented operator "
						+ c.getOperator());
			out.print(getWhereClause(c.getExpression1()) + op
					+ getWhereClause(c.getExpression2()));
		}
		out.close();
		return bytes.toString();
	}

	private String getWhereClause(NumericExpression<T> expression1) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		out.close();
		return bytes.toString();
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
