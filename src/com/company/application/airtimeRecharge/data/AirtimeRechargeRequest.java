package com.company.application.airtimeRecharge.data;

public class AirtimeRechargeRequest {
	private String paymentCode;
	private String customerId;
	private String fromAccountNumber;
	private String mobileNumber;
	private String mobileOperatorID;
	private String mobileOperatorDescription;
	private String transactionAmount;
	private String transactionDescription;	
	private String chargeAmount;
	private String taxAmount;	
	private String initiatingApp;
	
	
	public String getPaymentCode() {
		return paymentCode == null ? "" : paymentCode;
	}
	
	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}
	
	public String getCustomerId() {
		return customerId == null ? "" : customerId;
	}
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public String getFromAccountNumber() {
		return fromAccountNumber == null ? "" : fromAccountNumber;
	}
	
	public void setFromAccountNumber(String fromAccountNumber) {
		this.fromAccountNumber = fromAccountNumber;
	}
	
	public String getMobileNumber() {
		return mobileNumber == null ? "" : mobileNumber;
	}
	
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public String getMobileOperatorID() {
		return mobileOperatorID == null ? "" : mobileOperatorID;
	}
	
	public void setMobileOperatorID(String mobileOperatorID) {
		this.mobileOperatorID = mobileOperatorID;
	}
	
	public String getMobileOperatorDescription() {
		return mobileOperatorDescription == null ? "" : mobileOperatorDescription;
	}
	
	public void setMobileOperatorDescription(String mobileOperatorDescription) {
		this.mobileOperatorDescription = mobileOperatorDescription;
	}
	
	public String getTransactionAmount() {
		return transactionAmount == null ? "" : transactionAmount;
	}
	
	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	
	public String getTransactionDescription() {
		return transactionDescription == null ? "" : transactionDescription;
	}
	
	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}
	
	public String getChargeAmount() {
		return chargeAmount == null ? "" : chargeAmount;
	}
	
	public void setChargeAmount(String chargeAmount) {
		this.chargeAmount = chargeAmount;
	}
	
	public String getTaxAmount() {
		return taxAmount == null ? "" : taxAmount;
	}
	
	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}
	
	public String getInitiatingApp() {
		return initiatingApp == null ? "" : initiatingApp;
	}
	
	public void setInitiatingApp(String initiatingApp) {
		this.initiatingApp = initiatingApp;
	}
	
	
	
}