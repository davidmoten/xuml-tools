package xuml.tools.jaxb.compiler.actor;

import xuml.tools.jaxb.compiler.Entity;

public class Info {

	private Entity<?> currentEntity;

	public Entity<?> getCurrentEntity() {
		return currentEntity;
	}

	public void setCurrentEntity(Entity<?> entity) {
		this.currentEntity = entity;
	}

}
