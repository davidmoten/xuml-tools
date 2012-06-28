package xuml.tools.jaxb.compiler.message;

import xuml.tools.jaxb.compiler.Entity;

public class EntityCommit<T, R> implements HasEntity<T, R> {

	private final Entity<T, R> entity;

	public EntityCommit(Entity<T, R> entity) {
		super();
		this.entity = entity;
	}

	@Override
	public Entity<T, R> getEntity() {
		return entity;
	}
}
