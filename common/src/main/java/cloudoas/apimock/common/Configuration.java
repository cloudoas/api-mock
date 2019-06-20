package cloudoas.apimock.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	private final Properties prop = new Properties();
	
	private Configuration() {
		// immutable
	}
	
	public static Configuration fromResource(String resourceName) {
		Configuration config = new Configuration();
		config.load(resourceName);
		
		return config;
	}
	
	private void load(String resourceName) {
		try (InputStream configInput = Configuration.class.getClassLoader().getResourceAsStream(resourceName)){
			if (null!=configInput) {
				prop.load(configInput);
			}else {
				logger.warn("cannot find config file {} in the class path.", resourceName);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public String getString(String key) {
		return StringUtils.trimToEmpty(prop.getProperty(key));
	}
	
	public String getString(String key, String defaultValue) {
		String value = getString(key);
		
		return StringUtils.isBlank(value)?defaultValue:value;
	}
	
	public Integer getInt(String key) {
		Integer value = null;
		
		try {
			String text = getString(key);
			
			if (StringUtils.isNotBlank(text)) {
				value = Integer.valueOf(text);
			}
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		
		return value;
	}
	
	public Integer getInt(String key, Integer defaultValue) {
		Integer value = getInt(key);
		
		return null == value? defaultValue : value;
	}
}
