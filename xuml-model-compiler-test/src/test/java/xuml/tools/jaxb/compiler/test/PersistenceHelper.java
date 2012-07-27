package xuml.tools.jaxb.compiler.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.common.collect.Maps;

public class PersistenceHelper {

	public static enum DatabaseType {
		DERBY, H2, HSQLDB, OBJECT_DB;
	}

	public static EntityManagerFactory createEmf(String name,
			final String schema, DatabaseType type) {
		return Persistence.createEntityManagerFactory(name,
				getProperties(name, schema, type));
	}

	public static EntityManagerFactory createEmf(String name, String schema) {
		return createEmf(name, schema, DatabaseType.H2);
	}

	public static EntityManagerFactory createEmf(String name) {
		return createEmf(name, name.replace("-", "_"));
	}

	private static Map<String, String> getProperties(String name,
			String schema, DatabaseType type) {
		Map<String, String> map = Maps.newHashMap();
		map.put("hibernate.hbm2ddl.auto", "create-drop");
		map.put("hibernate.connection.pool_size", "5");
		map.put("hibernate.show_sql", "false");
		map.put("hibernate.format_sql", "true");
		if (type == DatabaseType.DERBY) {
			String dialect = "org.hibernate.dialect.DerbyDialect";
			String driver = "org.apache.derby.jdbc.EmbeddedDriver";
			String url = "jdbc:derby:memory:" + name + "-db;create=true";
			insertDatabaseSpecificProperties(map, dialect, driver, url);
		} else if (type == DatabaseType.H2) {
			String dialect = "org.hibernate.dialect.H2Dialect";
			String driver = "org.h2.Driver";
			String url = "jdbc:h2:mem:target/" + name
					+ "-db;INIT=CREATE SCHEMA IF NOT EXISTS " + schema;
			insertDatabaseSpecificProperties(map, dialect, driver, url);
		} else if (type == DatabaseType.HSQLDB) {
			// TODO does not work because does not create required schema
			String dialect = "org.hibernate.dialect.HSQLDialect";
			String driver = "org.hsqldb.jdbcDriver";
			String url = "jdbc:hsqldb:file:" + name + "-db";
			createSchema(url, schema);
			insertDatabaseSpecificProperties(map, dialect, driver, url);
		}
		return map;
	}

	private static void createSchema(String url, String schema) {
		try {
			Connection con = DriverManager.getConnection(url);
			con.createStatement().execute(
					"create schema " + schema + " authorization sa");
			con.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	private static void insertDatabaseSpecificProperties(
			Map<String, String> map, String dialect, String driver, String url) {
		map.put("hibernate.dialect", dialect);
		map.put("hibernate.connection.url", url);
		map.put("hibernate.connection.driver_class", driver);
	}

}
