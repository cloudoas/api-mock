package cloudoas.apimock.specstore.handler;

public class MockResponse {
	private String statusCode;
	private String contentType;
	private String responseBody;
	
	public MockResponse(String statusCode, String contentType, String responseBody) {
		super();
		this.statusCode = statusCode;
		this.contentType = contentType;
		this.responseBody = responseBody;
	}
	
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
}
