package xuml.tools.diagram;

import xuml.tools.datastore.DatastoreText;

public class ModelPersistence {

	private static final String KEY_KIND = "diagram";
	private static ModelPersistence instance = new ModelPersistence();

	public static ModelPersistence instance() {
		return instance;
	}

	public String getXml(String id) {
		String entity = id + "-model";
		String property = "model";
		// TODO change to DatastoreText.instance()
		DatastoreText ds = Context.instance().getDatastore();
		String result = ds.get(KEY_KIND, entity, property);
		return result;
	}

	public void save(String id, String xml) {
		String entity = id + "-model";
		String property = "model";
		// TODO change to DatastoreText.instance()
		DatastoreText ds = Context.instance().getDatastore();
		ds.put(KEY_KIND, entity, property, xml);
	}
}
