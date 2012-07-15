package xuml.tools.datastore;

/**
 * Factory pattern for creating {@link DatastoreText} instances.
 * 
 * @author dave
 * 
 */
public interface DatastoreTextFactory {

	/**
	 * Creates an instance of {@link DatastoreText}.
	 * 
	 * @return
	 */
	DatastoreText create();
}
