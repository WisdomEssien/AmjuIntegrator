package com.neptunesoftware.reuseableClasses.Quickteller.NameEnquiry;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class NameEnquiryResponse extends ResponseModel {

	private String accountName;

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public NameEnquiryResponse(String accountName) {
		super();
		this.accountName = accountName;
	}
	
	public NameEnquiryResponse(String responseCode, String resposonseMessage) {
		super(responseCode, resposonseMessage);
	}
	
	public NameEnquiryResponse() {
		super();
	}
	
}
