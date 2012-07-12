package xuml.tools.datastore.memory;

import java.util.Map;

import xuml.tools.datastore.DatastoreText;

import com.google.appengine.repackaged.com.google.common.collect.Maps;

public class DatastoreTextMemory implements DatastoreText {

	private final Map<String, String> entries = Maps.newConcurrentMap();

	@Override
	public void put(String kind, String name, String property, String value) {
		entries.put(getKey(kind, name, property), value);
	}

	private static String getKey(String kind, String name, String property) {
		return kind + ":" + name + ":" + property;
	}

	@Override
	public String get(String kind, String name, String property) {
		return entries.get(getKey(kind, name, property));
	}

}
