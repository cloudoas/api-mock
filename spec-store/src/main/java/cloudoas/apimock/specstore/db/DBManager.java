package cloudoas.apimock.specstore.db;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudoas.apimock.common.file.Configuration;
import cloudoas.apimock.common.file.Format;
import cloudoas.apimock.specstore.ConfigItems;
import cloudoas.apimock.specstore.Defaults;

public enum DBManager {
	INSTANCE;
	
	private static final Logger logger = LoggerFactory.getLogger(DBManager.class);
	public static final int ERROR=-1;
	
	private Configuration config;
	private Connection conn;
	
	private DBManager() {
		// singleton
	}
	
	public void init(Configuration config) {
		String driver = config.getString(ConfigItems.DB_DRIVER, Defaults.DB_DRIVER);
		
		 try {
		     Class.forName(driver);
		 } catch (Exception e) {
			 logger.error("Failed to load JDBC driver.", e);
		 }

		 Connection conn = null;
		 
		 String name = config.getString(ConfigItems.DB_NAME, Defaults.DB_NAME);
		 String username = config.getString(ConfigItems.DB_USERNAME, Defaults.DB_USERNAME);
		 String password = config.getString(ConfigItems.DB_PASSWORD, Defaults.DB_PASSWORD);
		 
		 try {
			conn = DriverManager.getConnection(String.format("jdbc:hsqldb:mem:%s", name), username, password);
		} catch (SQLException e) {
			 logger.error("Failed to create HSQLDB JDBC.", e);
		}
		 
		this.conn = conn;
		this.config = config;
	}
	
	public void close() {
		if (null!=conn) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}	
	
	public void createTables() {
		String scriptPath = config.getString(ConfigItems.SQL_SCRIPTS_CREATE, Defaults.SQL_SCRIPTS_CREATE);
		
		File scriptDir = new File(scriptPath);
		
		if (!scriptDir.exists() || !scriptDir.isDirectory()) {
			logger.error("Invalid script directory is specified. path={}", scriptPath);
			return;
		}
		
		File[] scripts = scriptDir.listFiles((dir, name)->StringUtils.endsWith(name.toUpperCase(), Format.SQL.name()));
		
		try (Statement stmt = conn.createStatement()){
			for (File script: scripts) {
				logger.info("running script {}", script.getName());
				String sql = FileUtils.readFileToString(script, Charset.defaultCharset());
				stmt.executeUpdate(sql);
			}
		} catch (Exception e) {
			logger.error("failed to create tables", e);
		}
	}
	
	public long insert(String sql, Consumer<PreparedStatement> parameterSetter) {
		if (logger.isDebugEnabled()) {
			logger.debug("[SQL]"+sql);
		}
		
		try(PreparedStatement insertStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			parameterSetter.accept(insertStmt);
			
			int rowCount = insertStmt.executeUpdate();
			
	        if (rowCount == 0) {
	            throw new SQLException("no rows affected: failed to execute insert statement: " + sql);
	        }

	        try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return generatedKeys.getLong(1);
	            }
	        }
		} catch (Exception e) {
            logger.error("failed to execute insert statement: " + sql, e);
        }
		
		return ERROR;
	}
	
	public void query(String sql, Consumer<PreparedStatement> parameterSetter, Consumer<ResultSet> resultSetConsumer) {
		if (logger.isDebugEnabled()) {
			logger.debug("[SQL]"+sql);
		}
		
		try(PreparedStatement queryStmt = conn.prepareStatement(sql)) {
			parameterSetter.accept(queryStmt);
			
			ResultSet rs = queryStmt.executeQuery();
			
	        if (null == rs) {
	            throw new SQLException("no result returned: failed to execute query statement: " + sql);
	        }

	        resultSetConsumer.accept(rs);
		} catch (Exception e) {
            logger.error("failed to execute insert statement: " + sql, e);
        }
	}
	
	public static String toDBString(String s) {
		return StringUtils.trimToEmpty(s).toLowerCase();
	}
}
