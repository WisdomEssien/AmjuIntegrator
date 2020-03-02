package com.neptunesoftware.reuseableClasses.WebserviceCall;

public class HttpResponse {

	public int statusCode;
	public String responseBody;
	
	public HttpResponse() {
	}
	
	public HttpResponse(int statusCode, String responseBody) {
		super();
		this.statusCode = statusCode;
		this.responseBody = responseBody;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
	
}
