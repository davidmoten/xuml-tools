package xuml.tools.gae;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

public class DatastoreText {
	
	private static DatastoreText datastore = new DatastoreText();
	
	public static DatastoreText instance(){
		return datastore;
	}

	public void put(String kind, String name, String property, String value) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Key k = KeyFactory.createKey(kind, name);
		System.out.println("putting " + k + "=" + value);
		Entity ent = new Entity(k);
		ent.setProperty(property, new Text(value));
		ds.put(ent);
	}

	public String get(String kind, String name, String property) {
		System.out.println("getting");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Key k = KeyFactory.createKey(kind, name);
		String result;
		try {
			Entity ent = ds.get(k);
			System.out.println(ent.getProperties());
			Text prop = (Text) ent.getProperty(property);
			System.out.println("Datastore.get "+ k + "= " + prop);
			if (prop != null)
				result = prop.getValue();
			else
				result = null;
		} catch (EntityNotFoundException e) {
			System.out.println(e.getMessage());
			result = null;
		}
		System.out.println("get returns " + result);
		return result;
	}

}
