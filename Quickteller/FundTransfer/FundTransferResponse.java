package com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer;

public class FundTransferResponse {
	private String responseCode;
	private String mac;
	private String transactionReference;
	private String transactionDate;
	private String responseCodeGrouping;
	private String pin;
	private String transferCode;
	
	public FundTransferResponse() {
	}
	public FundTransferResponse(String responseCode, String mac, String transactionReference, String transactionDate,
			String responseCodeGrouping, String pin, String transferCode) {
		
		this.responseCode = responseCode;
		this.mac = mac;
		this.transactionReference = transactionReference;
		this.transactionDate = transactionDate;
		this.responseCodeGrouping = responseCodeGrouping;
		this.pin = pin;
		this.transferCode = transferCode;
	}
	
	public String getResponseCode() {
		return responseCode == null ? "" : responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getTransactionReference() {
		return transactionReference;
	}
	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getResponseCodeGrouping() {
		return responseCodeGrouping;
	}
	public void setResponseCodeGrouping(String responseCodeGrouping) {
		this.responseCodeGrouping = responseCodeGrouping;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getTransferCode() {
		return transferCode;
	}
	public void setTransferCode(String transferCode) {
		this.transferCode = transferCode;
	}
	

	
}
