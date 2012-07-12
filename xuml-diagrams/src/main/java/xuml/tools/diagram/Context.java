package xuml.tools.diagram;

import xuml.tools.datastore.DatastoreText;
import xuml.tools.datastore.DatastoreTextFactory;
import xuml.tools.datastore.memory.DatastoreTextFactoryMemory;

public class Context {

	private static Context instance;

	public synchronized static Context instance() {
		if (instance == null)
			instance = new Context();
		return instance;
	}

	private Context() {
		// disable constructor outside of this class
		// must use static instance
	}

	private DatastoreTextFactory datastoreFactory = new DatastoreTextFactoryMemory();
	private DatastoreText datastore;

	public void setDatastoreFactory(DatastoreTextFactory datastoreFactory) {
		this.datastoreFactory = datastoreFactory;
	}

	public synchronized DatastoreText getDatastore() {
		if (datastore == null)
			datastore = datastoreFactory.create();
		return datastore;
	}
}
