package cloudoas.apimock.datafactory.model;

import java.util.HashMap;
import java.util.Map;

/**
 * A representation of mocked data of operations.
 *  key - name of response (e.g., http status code, or 'default')
 *  value - mocked responses
 * 
 * @author Daniel Zhao
 *
 */
public class OperationData {
	private Map<String, ResponseData> respMap = new HashMap<>();
	
	public Map<String, ResponseData> get() {
		return respMap;
	}

	public void add(String name, ResponseData responseData) {
		this.respMap.put(name, responseData);
	}
	
	@Override
	public String toString() {
		StringBuilder operationData = new StringBuilder("\n\t\t[OperationData]:\n");
		
		respMap.entrySet().forEach(entry->operationData.append(String.format("\t\t%s=>%s\n", entry.getKey(), entry.getValue().toString())));
	
		return operationData.toString();
	}
}
