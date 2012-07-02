package xuml.tools.model.compiler.runtime;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class Info {

	private Entity<?> currentEntity;

	public Entity<?> getCurrentEntity() {
		return currentEntity;
	}

	public void setCurrentEntity(Entity<?> entity) {
		this.currentEntity = entity;
	}

	private AtomicInteger counter = new AtomicInteger(0);

	public AtomicInteger getCounter() {
		return counter;
	}

	public void setCounter(AtomicInteger counter) {
		this.counter = counter;
	}

	private UUID id;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

}
