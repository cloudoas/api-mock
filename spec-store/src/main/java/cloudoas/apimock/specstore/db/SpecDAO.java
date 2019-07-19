package cloudoas.apimock.specstore.db;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudoas.apimock.common.file.Configuration;
import cloudoas.apimock.datafactory.ResponseDataFactory;
import cloudoas.apimock.datafactory.model.APIData;
import cloudoas.apimock.specstore.ConfigItems;

public enum SpecDAO {
	INSTANCE;
	
	private static final Logger logger = LoggerFactory.getLogger(SpecDAO.class);
	
	private DBManager dbManager;
	private Configuration config;
	
	public void init(DBManager dbManager, Configuration config) {
		this.dbManager = dbManager;
		this.config = config;
	}
	
	public long getSpecId(String specName, String version) {
		AtomicInteger result = new AtomicInteger(-1);
		
		dbManager.query(SQL.FIND_SPEC_ID, queryStmt->{
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
			
			return DBManager.ERROR;
		}
		
		long id = getSpecId(specName, version);
		
		if (id>=0) {
			return id;
		}
		
		return dbManager.insert(SQL.INSERT_SPEC, insertStmt->{
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
		
		dbManager.query(SQL.FIND_REQUEST_PATH_ID, queryStmt->{
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
			
			return DBManager.ERROR;
		}
		
		long id = getRequestPathId(path);
		
		if (id>=0) {
			return id;
		}
		
		return dbManager.insert(SQL.INSERT_REQUEST_PATH, insertStmt->{
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
		
		dbManager.query(SQL.FIND_CONTENT_TYPE_ID, queryStmt->{
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
			
			return DBManager.ERROR;
		}
		
		long id = getContentTypeId(contentType);
		
		if (id>=0) {
			return id;
		}
		
		return dbManager.insert(SQL.INSERT_CONTENT_TYPE, insertStmt->{
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
			
			return DBManager.ERROR;
		}
		
		return dbManager.insert(SQL.INSERT_RESP_BODY, insertStmt->{
			int index = 1;
			try {
				insertStmt.setString(index++, StringUtils.trimToEmpty(body));
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		});
	}
	
	public long getResponseBodyId(long specId, long pathId, long contentTypeId, String requestMethod, String responseName) {
		AtomicInteger result = new AtomicInteger(-1);
		
		dbManager.query(SQL.FIND_RESP_BODY_ID, queryStmt->{
			int index = 1;
			try {
				queryStmt.setLong(index++, specId);
				queryStmt.setLong(index++, pathId);
				queryStmt.setLong(index++, contentTypeId);
				queryStmt.setString(index++, requestMethod);
				queryStmt.setString(index++, responseName);
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
	
	public long addResponseIndex(long specId, long pathId, long contentTypeId, String requestMethod, String responseName, long respBodyId) {
		if (StringUtils.isBlank(responseName)) {
			logger.error("name is required.");
			
			return DBManager.ERROR;
		}
		
		return dbManager.insert(SQL.INSERT_RESP_INDEX, insertStmt->{
			int index = 1;
			try {
				insertStmt.setLong(index++, specId);
				insertStmt.setLong(index++, pathId);
				insertStmt.setLong(index++, contentTypeId);
				insertStmt.setString(index++, requestMethod);
				insertStmt.setString(index++, responseName);
				insertStmt.setLong(index++, respBodyId);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		});
	}
	
	public void addAPIData(APIData apiData) {
		long specId = addSpec(DBManager.toDBString(apiData.getSpecName()), DBManager.toDBString(apiData.getVersion()));
		
		apiData.getPathMap().forEach((path, pathData)->{
			long pathId = addRequestPath(DBManager.toDBString(path));
			
			pathData.get().forEach((method, operationData)->{
				
				operationData.get().forEach((name, responseData)->{
					responseData.get().forEach((contentType, mockedRepsonse)->{
						long contentTypeId = addContentType(DBManager.toDBString(contentType));
						
						long bodyId = getResponseBodyId(specId, pathId, contentTypeId, DBManager.toDBString(method.name()), DBManager.toDBString(name));
						
						if (bodyId<0) {// only add when not found
							long respBodyId = addResponseBody(mockedRepsonse.toString());
							
							addResponseIndex(specId, pathId, contentTypeId, DBManager.toDBString(method.name()), DBManager.toDBString(name), respBodyId);			
						}
					});
				});
			});
		});
	}
	
	public void loadLocalFile(File file) {
		if (logger.isDebugEnabled()) {
			logger.debug("loading file " + file.getName());
		}
		
		ResponseDataFactory dataFactory = new ResponseDataFactory();
		try {
			dataFactory.loadSpec(file);
			APIData mockData = dataFactory.makeData();
			
			addAPIData(mockData);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}		
	}
	
	public void loadLocalSpecFiles() {
		String loc = config.getString(ConfigItems.SPEC_LOCATION);
		
		if (StringUtils.isBlank(loc)) {
			logger.warn("No local spec.");
			return;
		}
		
		File specLoc = new File(loc);
		
		if (specLoc.isFile()) {
			loadLocalFile(specLoc);
		}else if (specLoc.isDirectory()) {
			File[] specs = specLoc.listFiles();
			
			for (int i=0; i<specs.length; ++i) {
				loadLocalFile(specs[i]);
			}
		}
	}
}
