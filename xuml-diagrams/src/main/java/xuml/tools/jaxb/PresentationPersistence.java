package xuml.tools.jaxb;

import xuml.tools.gae.DatastoreText;

public class PresentationPersistence {
	private static final String KEY_KIND = "diagram";
	private static PresentationPersistence instance = new PresentationPersistence();
	
	public static PresentationPersistence instance() {
		return  instance;
	}
	public String get(String id){
		String entity = id + "-presentation";
		String property = "presentation";
		DatastoreText ds = new DatastoreText();
		String result = ds.get(KEY_KIND, entity, property);
		return result;
	}
	
	public void save(String id, String xml){
		String entity = id+"-presentation";
		String property = "presentation";
		DatastoreText ds = new DatastoreText();
		ds.put(KEY_KIND, entity, property, xml);
	}
}
