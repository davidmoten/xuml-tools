package xuml.tools.datastore.memory;

import xuml.tools.datastore.DatastoreText;
import xuml.tools.datastore.DatastoreTextFactory;

public class DatastoreTextFactoryMemory implements DatastoreTextFactory {

    @Override
    public DatastoreText create() {
        return new DatastoreTextMemory();
    }

}
