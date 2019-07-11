package cloudoas.apimock.specstore.db;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudoas.apimock.common.Configuration;
import cloudoas.apimock.common.FileInfo;
import cloudoas.apimock.specstore.ConfigItems;
import cloudoas.apimock.specstore.Defaults;

public class DataManager {
	private static final Logger logger = LoggerFactory.getLogger(DataManager.class);
	private static final Configuration config = Configuration.fromResource(ConfigItems.CONFIG_NAME);
	
	public static final int ERROR=-1;
	
	protected Connection conn;
	
	public void initialize() {
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
		
		File[] scripts = scriptDir.listFiles((dir, name)->StringUtils.endsWith(name, FileInfo.SQL_EXT));
		
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
	
	protected long insert(String sql, Consumer<PreparedStatement> parameterSetter) {
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
	
	protected void query(String sql, Consumer<PreparedStatement> parameterSetter, Consumer<ResultSet> resultSetConsumer) {
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
	
	public long getSpecId(String specName, String version) {
		AtomicInteger result = new AtomicInteger(-1);
		
		query(SQL.FIND_SPEC_ID, queryStmt->{
			int index = 1;
			try {
				queryStmt.setString(index++, specName);
				queryStmt.setString(index++, version);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}, resultSet->{
			try {
				if (resultSet.next()) {
					result.set(resultSet.getInt(1));
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		});
		
		return result.longValue();	
	}
	
	public long addSpec(String specName, String version) {
		if (StringUtils.isBlank(specName)) {
			logger.error("specName is required.");
			
			return ERROR;
		}
		
		long id = getSpecId(specName, version);
		
		if (id>=0) {
			return id;
		}
		
		return insert(SQL.INSERT_SPEC, insertStmt->{
			int index = 1;
			try {
				insertStmt.setString(index++, StringUtils.trimToEmpty(specName));
				insertStmt.setString(index++, StringUtils.trimToEmpty(version));
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		});
	}
	
	public long getRequestPathId(String path) {
		AtomicInteger result = new AtomicInteger(-1);
		
		query(SQL.FIND_REQUEST_PATH_ID, queryStmt->{
			int index = 1;
			try {
				queryStmt.setString(index++, path);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}, resultSet->{
			try {
				if (resultSet.next()) {
					result.set(resultSet.getInt(1));
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		});
		
		return result.longValue();	
	}
	
	public long addRequestPath(String path) {
		if (StringUtils.isBlank(path)) {
			logger.error("path is required.");
			
			return ERROR;
		}
		
		long id = getRequestPathId(path);
		
		if (id>=0) {
			return id;
		}
		
		return insert(SQL.INSERT_REQUEST_PATH, insertStmt->{
			int index = 1;
			try {
				insertStmt.setString(index++, StringUtils.trimToEmpty(path));
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		});
	}
	
	public long getContentTypeId( String contentType) {
		AtomicInteger result = new AtomicInteger(-1);
		
		query(SQL.FIND_CONTENT_TYPE_ID, queryStmt->{
			int index = 1;
			try {
				queryStmt.setString(index++, contentType);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}, resultSet->{
			try {
				if (resultSet.next()) {
					result.set(resultSet.getInt(1));
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		});
		
		return result.longValue();
	}
	
	public long addContentType(String contentType) {
		if (StringUtils.isBlank(contentType)) {
			logger.error("contentType is required.");
			
			return ERROR;
		}
		
		long id = getContentTypeId(contentType);
		
		if (id>=0) {
			return id;
		}
		
		return insert(SQL.INSERT_CONTENT_TYPE, insertStmt->{
			int index = 1;
			try {
				insertStmt.setString(index++, StringUtils.trimToEmpty(contentType));
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		});
	}
	
	public long addResponseBody(String body) {
		if (StringUtils.isBlank(body)) {
			logger.error("body is empty.");
			
			return ERROR;
		}
		
		return insert(SQL.INSERT_RESP_BODY, insertStmt->{
			int index = 1;
			try {
				insertStmt.setString(index++, StringUtils.trimToEmpty(body));
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		});
	}
	
	public long getResponseBodyId(long specId, long pathId, long contentTypeId, String name) {
		AtomicInteger result = new AtomicInteger(-1);
		
		query(SQL.FIND_RESP_BODY_ID, queryStmt->{
			int index = 1;
			try {
				queryStmt.setLong(index++, specId);
				queryStmt.setLong(index++, pathId);
				queryStmt.setLong(index++, contentTypeId);
				queryStmt.setString(index++, name);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}, resultSet->{
			try {
				if (resultSet.next()) {
					result.set(resultSet.getInt(1));
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		});
		
		return result.longValue();
	}
	
	public long addResponseIndex(long specId, long pathId, long contentTypeId, long respBodyId, String name) {
		if (StringUtils.isBlank(name)) {
			logger.error("name is required.");
			
			return ERROR;
		}
		
		return insert(SQL.INSERT_RESP_INDEX, insertStmt->{
			int index = 1;
			try {
				insertStmt.setLong(index++, specId);
				insertStmt.setLong(index++, pathId);
				insertStmt.setLong(index++, contentTypeId);
				insertStmt.setString(index++, name);
				insertStmt.setLong(index++, respBodyId);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		});
	}
	
	public void addResponse(String specName, String version, String path, String contentType, String name, String body) {
		long specId = addSpec(toDBString(specName), toDBString(version));
		long pathId = addRequestPath(toDBString(path));
		long contentTypeId = addContentType(toDBString(contentType));
		
		if (specId<0 || pathId<0 || contentTypeId<0) {
			logger.error("invalid id returned. {}, {}, {}", specId, pathId, contentTypeId);
			
			return;
		}
		
		long bodyId = getResponseBodyId(specId, pathId, contentTypeId, toDBString(name));
		
		if (bodyId<0) {// only add when not found
			long respBodyId = addResponseBody(body);
			
			addResponseIndex(specId, pathId, contentTypeId, respBodyId, toDBString(name));			
		}
	}
	
	protected String toDBString(String s) {
		return StringUtils.trimToEmpty(s).toLowerCase();
	}
}
