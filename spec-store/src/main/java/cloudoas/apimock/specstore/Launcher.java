package cloudoas.apimock.specstore;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudoas.apimock.common.Configuration;

public class Launcher {
	private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
	private static final Configuration config = Configuration.fromResource(ConfigItems.CONFIG_NAME);
	
	
	public Connection initializeDB() {
		String driver = config.getString(ConfigItems.DB_DRIVER, Defaults.DB_DRIVER);
		
		 try {
		     Class.forName(driver);
		 } catch (Exception e) {
			 logger.error("Failed to load JDBC driver.", e);
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
	
	public void createTables(Connection conn) {
		if (null==conn) {
			logger.error("Do not have a valid DB connection");
			return;
		}
		
		String scriptPath = config.getString(ConfigItems.SQL_SCRIPTS_CREATE, Defaults.SQL_SCRIPTS_CREATE);
		
		File scriptDir = new File(scriptPath);
		
		if (!scriptDir.exists() || !scriptDir.isDirectory()) {
			logger.error("Invalid script directory is specified. path={}", scriptPath);
			return;
		}
		
		File[] scripts = scriptDir.listFiles((dir, name)->StringUtils.endsWith(name, FileConstants.SQL_EXT));
		
		for (File script: scripts) {
			try (Statement stmt = conn.createStatement()){
				String sql = FileUtils.readFileToString(script, Charset.defaultCharset());
				
				stmt.executeUpdate(sql);
			} catch (Exception e) {
				logger.error("failed to execute sql {}", e);
			}
		}
	}
	
	public static void main(String[] args) {
	}
}
