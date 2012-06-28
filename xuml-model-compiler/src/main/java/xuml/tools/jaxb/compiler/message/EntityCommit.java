package xuml.tools.jaxb.compiler.message;

import xuml.tools.jaxb.compiler.Entity;

public class EntityCommit<T> implements HasEntity<T> {

	private final Entity<T> entity;

	public EntityCommit(Entity<T> entity) {
		this.entity = entity;
	}

	@Override
	public Entity<T> getEntity() {
		return entity;
	}
}
