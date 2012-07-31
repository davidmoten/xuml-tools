package xuml.tools.model.compiler.runtime.query;

import static org.junit.Assert.assertEquals;
import static xuml.tools.model.compiler.runtime.query.SelectBuilder.builder;

import java.io.Serializable;

import org.junit.Test;

import xuml.tools.model.compiler.runtime.Entity;
import xuml.tools.model.compiler.runtime.EntityHelper;
import xuml.tools.model.compiler.runtime.Event;
import akka.util.Duration;

public class SelectBuilderTest {

	private final NumericExpressionField<Ent> f = new NumericExpressionField<Ent>(
			new Field("field"));

	private final StringExpressionField<Ent> g = new StringExpressionField<Ent>(
			new Field("field"));

	@Test
	public void testClauseGenerationEquals() {
		assertEquals("(field = :_p1)", builder(f.eq(3)).getClause());
	}

	@Test
	public void testClauseGenerationLessThan() {
		assertEquals("(field < :_p1)", builder(f.lt(3)).getClause());
	}

	@Test
	public void testClauseGenerationLessThanOrEquals() {
		assertEquals("(field <= :_p1)", builder(f.lte(3)).getClause());
	}

	@Test
	public void testClauseGenerationGreaterThan() {
		assertEquals("(field > :_p1)", builder(f.gt(3)).getClause());
	}

	@Test
	public void testClauseGenerationGreaterThanOrEquals() {
		assertEquals("(field >= :_p1)", builder(f.gte(3)).getClause());
	}

	@Test
	public void testClauseGenerationAnd() {
		assertEquals("((field > :_p1) and (field < :_p2))",
				builder(f.gt(1).and(f.lt(3))).getClause());
	}

	@Test
	public void testClauseGenerationOr() {
		assertEquals("((field > :_p1) or (field < :_p2))",
				builder(f.gt(1).or(f.lt(3))).getClause());
	}

	@Test
	public void testClauseGenerationPlus() {
		assertEquals("((field + field) <= :_p1)", builder(f.plus(f).lte(3))
				.getClause());
	}

	@Test
	public void testClauseGenerationMinus() {
		assertEquals("((field - field) <= :_p1)", builder(f.minus(f).lte(3))
				.getClause());
	}

	@Test
	public void testClauseGenerationTimes() {
		assertEquals("((field * field) <= :_p1)", builder(f.times(f).lte(3))
				.getClause());
	}

	@Test
	public void testClauseGenerationDivide() {
		assertEquals("((field / field) <= :_p1)", builder(f.divide(f).lte(3))
				.getClause());
	}

	@Test
	public void testSqlGivenNonEmptyClause() {
		assertEquals("select e from Ent e where (field=1)",
				SelectBuilder.getSql(Ent.class, "(field=1)"));
	}

	@Test
	public void testSqlGivenEmptyClause() {
		assertEquals("select e from Ent e", SelectBuilder.getSql(Ent.class, ""));
	}

	@Test
	public void testClauseGenerationStringEquals() {
		assertEquals("(field = :_p1)", builder(g.eq("hello")).getClause());
	}

	/*********************/
	/** Utility Methods **/
	/*********************/

	private static class Ent implements Entity<Ent> {

		@Override
		public Serializable getId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String uniqueId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Ent signal(Event<Ent> event) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Ent signal(Event<Ent> event, Duration delay) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Ent event(Event<Ent> event) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public EntityHelper helper() {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
