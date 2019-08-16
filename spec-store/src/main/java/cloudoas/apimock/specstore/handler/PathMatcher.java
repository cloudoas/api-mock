package cloudoas.apimock.specstore.handler;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

public class PathMatcher {
	
	public static String match(Collection<String> paths, String path) {
		return paths.stream().filter(p->StringUtils.equalsIgnoreCase(p, path)).findFirst().orElse(null);
	}
}
