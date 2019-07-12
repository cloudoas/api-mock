package cloudoas.apimock.datafactory.model;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.models.PathItem.HttpMethod;

public class PathData {
	private Map<HttpMethod, OperationData> operationData = new HashMap<>();

	public Map<HttpMethod, OperationData> getOperationData() {
		return operationData;
	}

	public void addOperationData(HttpMethod method, OperationData data) {
		this.operationData.put(method, data);
	}
	
	@Override
	public String toString() {
		StringBuilder pathData = new StringBuilder("\n\t[PathData]:\n");
		
		operationData.entrySet().forEach(entry->pathData.append(String.format("\t%s=>%s\n", entry.getKey().name(), entry.getValue().toString())));
	
		return pathData.toString();
	}
}
