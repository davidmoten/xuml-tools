package xuml.tools.model.compiler.runtime;


public interface Entity<T> {

	/**
	 * Returns a string that uniquely represents an individual entity in the
	 * database.
	 * 
	 * @return
	 */
	String uniqueId();

	/**
	 * All events go through here. ThreadLocal will be used to detect if an
	 * event is to self or not. Events to self are queued up to run
	 * synchronously after the on-entry procedure on this entity associated with
	 * the event is run and before the transaction is committed. Only after the
	 * transaction is committed successfully will the signals to other entities
	 * that were made by the on-entry procedure be sent.
	 * 
	 * @param event
	 */
	T signal(Event<T> event);

	/**
	 * Runs the on-entry procedure associated with this event. No transaction is
	 * opened around the on-entry procedure. This method should be used for unit
	 * testing purposes only. Please use the signal method instead.
	 * 
	 * @param event
	 */
	T event(Event<T> event);

	/**
	 * Returns a helper instance for this entity. The helper keeps track of
	 * calls to self and to other entities during an on-entry procedure and
	 * ensures they are run at the appropriate time.
	 * 
	 * @return
	 */
	EntityHelper helper();

}
