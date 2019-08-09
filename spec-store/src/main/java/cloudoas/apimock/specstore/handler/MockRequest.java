package cloudoas.apimock.specstore.handler;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MockRequest {
	@JsonProperty(required=true)
	private String specName;
	@JsonProperty(required=true)
	private String version;
	@JsonProperty(required=true)
	private String requestMethod;
	@JsonProperty(required=true)
	private String requestPath;
	private String contentType;
	private String statusCode;
	
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
	public String getRequestMethod() {
		return requestMethod;
	}
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
	public String getRequestPath() {
		return requestPath;
	}
	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
