package xuml.tools.model.compiler.message;

import xuml.tools.model.compiler.Entity;

public class CloseEntityActor {
	private final Entity<?> entity;

	public CloseEntityActor(Entity<?> entity) {
		this.entity = entity;
	}

	public Entity<?> getEntity() {
		return entity;
	}
}
