package xuml.tools.datastore;

public interface DatastoreText {

	public abstract void put(String kind, String name, String property,
			String value);

	public abstract String get(String kind, String name, String property);

}