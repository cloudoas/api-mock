package io.github.danielzcoding.apibot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	private static final Properties prop = new Properties();
	private static final String CONFIG_NAME = "api-bot.properties";
	
	public static final String SERVER_HOST = "server.host";
	public static final String SERVER_PORT = "server.port";
	
	static {
		InputStream configInput = Configuration.class.getClassLoader().getResourceAsStream(CONFIG_NAME);
		
		try {
			prop.load(configInput);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static String getString(String key) {
		return StringUtils.trimToEmpty(prop.getProperty(key));
	}
	
	public static String getString(String key, String defaultValue) {
		String value = getString(key);
		
		return StringUtils.isBlank(value)?defaultValue:value;
	}
	
	public static Integer getInt(String key) {
		Integer value = null;
		
		try {
			value = Integer.valueOf(getString(key));
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		
		return value;
	}
	
	public static Integer getInt(String key, Integer defaultValue) {
		Integer value = getInt(key);
		
		return null == value? defaultValue : value;
	}
}
