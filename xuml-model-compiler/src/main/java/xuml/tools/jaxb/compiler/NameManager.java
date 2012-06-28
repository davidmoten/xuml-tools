package xuml.tools.jaxb.compiler;

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;

public class NameManager {

	private static NameManager instance;

	private Set<String> reservedWords;

	// Class -> (OtherClass+RNum/ThisClass+FieldName<->FieldName)
	private final Map<String, BiMap<String, String>> referenceFields = newHashMap();

	private final Map<String, BiMap<String, String>> referencedColumns = newHashMap();

	private final Map<String, BiMap<String, String>> referencedTables = newHashMap();

	private NameManager() {
		try {
			reservedWords = Sets.newHashSet();
			// the list below was obtained from http://drupal.org/node/141051
			// and is a combination of sql reserved words from the following
			// standards/databases:
			// ANSI SQL 92, ANS SQL 99, ANSI SQL 2003, MySQL 3.23.x, MySQL 4.x,
			// MySQL 5.x, PostGreSQL 8.1, MS SQL Server 2000, MS ODBC, Oracle
			// 10.2
			InputStream in = NameManager.class
					.getResourceAsStream("/database-reserved-words.txt");
			Preconditions.checkNotNull(in, "reserved words not found!");
			for (String line : CharStreams.readLines(new InputStreamReader(in))) {
				reservedWords.add(line.trim().toUpperCase());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized static NameManager getInstance() {
		if (null == instance) {
			instance = new NameManager();
		}
		return instance;
	}

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
			if (map.inverse().get(columnName) != null
					|| isReservedWord(columnName)) {
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

	public String toTableName(String schema, String className) {
		if (referencedTables.get(schema) == null) {
			BiMap<String, String> bimap = HashBiMap.create();
			referencedTables.put(schema, bimap);
		}
		BiMap<String, String> map = referencedTables.get(schema);
		String key = getKey(schema, className);
		if (map.get(key) != null)
			return map.get(key);
		else {
			String optimalName = Util.lowerFirst(Util.toColumnName(className));
			String name = optimalName;
			if (map.inverse().get(name) != null || isReservedWord(name)) {
				int i = 1;
				while (map.inverse().get(name + "_" + i) != null) {
					i++;
				}
				name = name + "_" + i;
			}
			map.put(key, name);
			return name;
		}
	}

	private boolean isReservedWord(String name) {
		return reservedWords.contains(name.toUpperCase());
	}
}
