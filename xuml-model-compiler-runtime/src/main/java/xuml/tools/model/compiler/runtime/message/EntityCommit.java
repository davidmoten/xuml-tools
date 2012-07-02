package xuml.tools.model.compiler.runtime.message;

import xuml.tools.model.compiler.runtime.Entity;

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
