package xuml.tools.diagram;

import xuml.tools.datastore.DatastoreText;

public class PresentationPersistence {
	private static final String KEY_KIND = "diagram";
	private static PresentationPersistence instance = new PresentationPersistence();

	public static PresentationPersistence instance() {
		return instance;
	}

	public String get(String id) {
		String entity = id + "-presentation";
		String property = "presentation";
		DatastoreText ds = Context.instance().getDatastore();
		String result = ds.get(KEY_KIND, entity, property);
		return result;
	}

	public void save(String id, String xml) {
		String entity = id + "-presentation";
		String property = "presentation";
		DatastoreText ds = Context.instance().getDatastore();
		ds.put(KEY_KIND, entity, property, xml);
	}
}
