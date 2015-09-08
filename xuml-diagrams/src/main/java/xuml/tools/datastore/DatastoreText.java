package xuml.tools.datastore;

/**
 * Stores and retrieves String properties.
 * 
 * @author dave
 * 
 */
public interface DatastoreText {

    /**
     * Stores a property.. The kind, name and property can be thought of as
     * corresponding roughly to table,column, row key.
     * 
     * @param kind
     * @param name
     * @param property
     * @param value
     */
    void put(String kind, String name, String property, String value);

    /**
     * Returns a property value keyed by kind,name and property.
     * 
     * @param kind
     * @param name
     * @param property
     * @return
     */
    String get(String kind, String name, String property);

}