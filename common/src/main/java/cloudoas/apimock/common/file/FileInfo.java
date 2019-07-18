package cloudoas.apimock.common.file;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

public interface FileInfo {
	String DOT=".";

	static String getExtension(File file) {
	    String name = file.getName();
	    int lastIndexOf = name.lastIndexOf(DOT);
	    if (lastIndexOf < 0) {
	        return StringUtils.EMPTY;
	    }
	    return name.substring(lastIndexOf+1);
	}
	
	static String getName(File file) {
	    String name = file.getName();
	    int lastIndexOf = name.lastIndexOf(DOT);
	    
	    if (lastIndexOf < 0) {
	        return name;
	    }
	    return name.substring(0, lastIndexOf);	    
	}
}
