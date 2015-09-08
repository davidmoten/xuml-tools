package xuml.tools.datastore.gae;

import xuml.tools.datastore.DatastoreText;
import xuml.tools.datastore.DatastoreTextFactory;

public class DatastoreTextFactoryGae implements DatastoreTextFactory {

    @Override
    public DatastoreText create() {
        return new DatastoreTextGae();
    }

}
