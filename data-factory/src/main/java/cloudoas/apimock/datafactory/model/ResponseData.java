package cloudoas.apimock.datafactory.model;

import java.util.HashMap;
import java.util.Map;

/**
 * A representation of mocked data of responses.
 *  key - content type
 *  value - mocked response
 * 
 * @author Daniel Zhao
 *
 */
public class ResponseData {
	private Map<String, Object> contentMap = new HashMap<>();

	public Map<String, Object> get() {
		return contentMap;
	}

	public void add(String contentType, Object mockedRepsonse) {
		this.contentMap.put(contentType, mockedRepsonse);
	}
}
