package xuml.tools.jaxb.compiler;

public interface Entity<T, R> {

	/**
	 * Returns the Entity id (which might be a composite).
	 * 
	 * @return
	 */
	R getId();

	/**
	 * All events go through here. ThreadLocal will be used to detect if an
	 * event is to self or not.
	 * 
	 * @param event
	 */
	void signal(Event<T> event);

	/**
	 * Synchronously signal an entity.
	 * 
	 * @param event
	 */
	void event(Event<T> event);

}
