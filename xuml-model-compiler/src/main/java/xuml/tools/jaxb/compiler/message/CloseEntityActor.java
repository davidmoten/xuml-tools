package xuml.tools.jaxb.compiler.message;

import xuml.tools.jaxb.compiler.Entity;

public class CloseEntityActor {
	private final Entity<?> entity;

	public CloseEntityActor(Entity<?> entity) {
		this.entity = entity;
	}

	public Entity<?> getEntity() {
		return entity;
	}
}
