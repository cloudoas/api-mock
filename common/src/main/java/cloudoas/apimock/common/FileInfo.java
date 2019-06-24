package cloudoas.apimock.common;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

public interface FileInfo {
	String DOT=".";
	String SQL_EXT=".sql";
	String JSON_EXT=".json";
	String YML_EXT=".yml";
	String YAML_EXT=".yaml";
	
	static String getExtension(File file) {
	    String name = file.getName();
	    int lastIndexOf = name.lastIndexOf(DOT);
	    if (lastIndexOf < 0) {
	        return StringUtils.EMPTY;
	    }
	    return name.substring(lastIndexOf);
	}
}
