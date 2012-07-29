package xuml.tools.model.compiler.runtime.query;

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
		// TODO implement getResults
		return Lists.newArrayList();
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
