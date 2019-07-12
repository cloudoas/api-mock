package cloudoas.apimock.datafactory.model;

import java.util.HashMap;
import java.util.Map;

public class OperationData {
	private Map<String, Object> responseData = new HashMap<>();
	
	public Map<String, Object> getResponseData() {
		return responseData;
	}

	public void addResponseData(String name, Object responseData) {
		this.responseData.put(name, responseData);
	}
	
	@Override
	public String toString() {
		StringBuilder operationData = new StringBuilder("\n\t\t[OperationData]:\n");
		
		responseData.entrySet().forEach(entry->operationData.append(String.format("\t\t%s=>%s\n", entry.getKey(), entry.getValue().toString())));
	
		return operationData.toString();
	}
}
