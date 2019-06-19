package cloudoas.apimock.specstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudoas.apimock.common.Configuration;

public class Launcher {
	private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
	private static final Configuration config = Configuration.fromResource(ConfigItems.CONFIG_NAME);
	
	
	public Connection initializeDB() {
		 try {
		     Class.forName("org.hsqldb.jdbc.JDBCDriver" );
		 } catch (Exception e) {
			 logger.error("Failed to load HSQLDB JDBC driver.", e);
		     return null;
		 }

		 Connection conn = null;
		 
		 String name = config.getString(ConfigItems.DB_NAME, Defaults.DB_NAME);
		 String username = config.getString(ConfigItems.DB_USERNAME, Defaults.DB_USERNAME);
		 String password = config.getString(ConfigItems.DB_PASSWORD, Defaults.DB_PASSWORD);
		 
		 try {
			conn = DriverManager.getConnection(String.format("jdbc:hsqldb:mem:%s", name), username, password);
		} catch (SQLException e) {
			 logger.error("Failed to create HSQLDB JDBC.", e);
		     return null;
		}
		 
		 return conn;
	}
	
	public static void main(String[] args) {
	}
}
