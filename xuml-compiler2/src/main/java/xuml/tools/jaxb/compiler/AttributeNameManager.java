package xuml.tools.jaxb.compiler;

import static com.google.common.collect.Maps.newHashMap;

import java.math.BigInteger;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class AttributeNameManager {

	private static AttributeNameManager instance;

	private AttributeNameManager() {
	}

	public synchronized static AttributeNameManager getInstance() {
		if (null == instance) {
			instance = new AttributeNameManager();
		}
		return instance;
	}

	// Class -> (OtherClass+RNum/ThisClass+FieldName<->FieldName)
	private final Map<String, BiMap<String, String>> referenceFields = newHashMap();

	private final Map<String, BiMap<String, String>> referencedColumns = newHashMap();

	public String toFieldName(String cls, String viewedClass, BigInteger rNum) {
		if (referenceFields.get(cls) == null) {
			BiMap<String, String> bimap = HashBiMap.create();
			referenceFields.put(cls, bimap);
		}
		BiMap<String, String> map = referenceFields.get(cls);
		String key = getKey(viewedClass, rNum);
		if (map.get(key) != null)
			return map.get(key);
		else {
			String optimalFieldName = Util.lowerFirst(Util
					.toJavaIdentifier(viewedClass));
			String currentKey = map.inverse().get(optimalFieldName);
			String fieldName;
			if (currentKey == null)
				fieldName = optimalFieldName;
			else
				fieldName = optimalFieldName + "_R" + rNum;
			map.put(key, fieldName);
			return fieldName;
		}
	}

	public String toFieldName(String cls, String attributeName) {
		if (referenceFields.get(cls) == null) {
			BiMap<String, String> bimap = HashBiMap.create();
			referenceFields.put(cls, bimap);
		}
		BiMap<String, String> map = referenceFields.get(cls);
		String key = getKey(cls, attributeName);
		if (map.get(key) != null)
			return map.get(key);
		else {
			String optimalFieldName = Util.lowerFirst(Util
					.toJavaIdentifier(attributeName));
			String fieldName = optimalFieldName;
			if (map.inverse().get(fieldName) != null) {
				int i = 1;
				while (map.inverse().get(fieldName + i) != null) {
					i++;
				}
				fieldName = fieldName + i;
			}
			map.put(key, fieldName);
			return fieldName;
		}
	}

	public String toColumnName(String cls, String attributeName) {
		if (referencedColumns.get(cls) == null) {
			BiMap<String, String> bimap = HashBiMap.create();
			referencedColumns.put(cls, bimap);
		}
		BiMap<String, String> map = referencedColumns.get(cls);
		String key = getKey(cls, attributeName);
		if (map.get(key) != null)
			return map.get(key);
		else {
			String optimalColumnName = Util.lowerFirst(Util
					.toColumnName(attributeName));
			String columnName = optimalColumnName;
			if (map.inverse().get(columnName) != null) {
				int i = 1;
				while (map.inverse().get(columnName + "_" + i) != null) {
					i++;
				}
				columnName = columnName + "_" + i;
			}
			map.put(key, columnName);
			return columnName;
		}
	}

	private static String getKey(String cls, String attributeName) {
		return cls + "_._" + attributeName;
	}

	private static String getKey(String viewedClass, BigInteger rnum) {
		return viewedClass + "_._R" + rnum;
	}
}
