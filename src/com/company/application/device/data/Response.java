package com.company.application.device.data;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class Response extends ResponseModel {

	private String detail;
	
	public Response() {
		super();
	}
	
	public Response(String responseCode, String responseMessage, String detail) {
		super(responseCode, responseMessage);
		this.detail = detail;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	
}
