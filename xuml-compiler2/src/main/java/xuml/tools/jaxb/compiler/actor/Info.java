package xuml.tools.jaxb.compiler.actor;

import xuml.tools.jaxb.compiler.Entity;
import akka.actor.ActorRef;

public class Info {

	private ActorRef currentActor;
	private Entity<?, ?> currentEntity;

	public ActorRef getCurrentActor() {
		return currentActor;
	}

	public void setCurrentActor(ActorRef currentActor) {
		this.currentActor = currentActor;
	}

	public Entity<?, ?> getCurrentEntity() {
		return currentEntity;
	}

	public void setCurrentEntity(Entity<?, ?> entity) {
		this.currentEntity = entity;
	}

}
