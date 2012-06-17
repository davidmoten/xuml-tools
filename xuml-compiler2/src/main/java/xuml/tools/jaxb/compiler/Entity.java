package xuml.tools.jaxb.compiler;

public interface Entity<T, R> {

	/**
	 * Returns the Entity id (which might be a composite).
	 * 
	 * @return
	 */
	R getId();

	/**
	 * All events go through here.s
	 * 
	 * @param event
	 */
	void event(Event<T> event);
}
