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

	@Test
	public void testClauseGenerationLessThan() {
		assertEquals("(field<3)", builder(f.lt(3)).getClause());
	}

	@Test
	public void testClauseGenerationLessThanOrEquals() {
		assertEquals("(field<=3)", builder(f.lte(3)).getClause());
	}

	@Test
	public void testClauseGenerationGreaterThan() {
		assertEquals("(field>3)", builder(f.gt(3)).getClause());
	}

	@Test
	public void testClauseGenerationGreaterThanOrEquals() {
		assertEquals("(field>=3)", builder(f.gte(3)).getClause());
	}

	@Test
	public void testClauseGenerationAnd() {
		assertEquals("((field>1) and (field<3))", builder(f.gt(1).and(f.lt(3)))
				.getClause());
	}

	@Test
	public void testClauseGenerationOr() {
		assertEquals("((field>1) or (field<3))", builder(f.gt(1).or(f.lt(3)))
				.getClause());
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
