package cloudoas.apimock.specstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpecInfo {
	private String url;
	private String content;
	
	@JsonProperty(required=true)
	private String format;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
}
