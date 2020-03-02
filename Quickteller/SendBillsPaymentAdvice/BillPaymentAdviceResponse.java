package com.neptunesoftware.reuseableClasses.Quickteller.SendBillsPaymentAdvice;

public class BillPaymentAdviceResponse {
	private String transactionRef;
	private String responseCode;
	private String responseMessage;
	
	public BillPaymentAdviceResponse() {
		
	}
	
	public BillPaymentAdviceResponse(String transactionRef, String responseCode, String responseMessage) {
		this.transactionRef = transactionRef;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}
	
	public String getTransactionRef() {
		return transactionRef == null ? "" : transactionRef;
	}
	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}
	public String getResponseCode() {
		return responseCode == null ? "" : responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage == null ? "" : responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
}
