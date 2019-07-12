package cloudoas.apimock.datafactory.model;

import java.util.HashMap;
import java.util.Map;

public class APIData {
	private String specName;
	private String version;
	private Map<String, PathData> pathData = new HashMap<>();
	
	public String getSpecName() {
		return specName;
	}
	public void setSpecName(String specName) {
		this.specName = specName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public  Map<String, PathData> getPathData() {
		return pathData;
	}
	public void addPathData(String path, PathData pathData) {
		this.pathData.put(path, pathData);
	}
	
	@Override
	public String toString() {
		StringBuilder apiData = new StringBuilder("[APIData]:\n");
		
		apiData.append(String.format("specName=>%s\nversion=>%s\n", specName, version));
		
		pathData.entrySet().forEach(entry->apiData.append(String.format("%s=>%s\n", entry.getKey(), entry.getValue().toString())));
	
		return apiData.toString();
	}
}
