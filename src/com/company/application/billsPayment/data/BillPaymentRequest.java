package com.company.application.billsPayment.data;

public class BillPaymentRequest {
	
	private String initiatingApp;
	private String paymentCode;
	private String customerId;
	private String fromAccountNumber;
	private String billerCategoryID;
	private String billerDescription;
	private String transactionAmount;
	private String transactionDescription;
	private String cardNumber;	
	private String chargeAmount;
	private String taxAmount;
	
	public String getInitiatingApp() {
		return initiatingApp;
	}
	
	public void setInitiatingApp(String initiatingApp) {
		this.initiatingApp = initiatingApp;
	}
	
	public String getPaymentCode() {
		return paymentCode;
	}
	
	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}
	
	public String getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public String getFromAccountNumber() {
		return fromAccountNumber;
	}
	
	public void setFromAccountNumber(String fromAccountNumber) {
		this.fromAccountNumber = fromAccountNumber;
	}
	
	public String getBillerCategoryID() {
		return billerCategoryID;
	}
	
	public void setBillerCategoryID(String billerCategoryID) {
		this.billerCategoryID = billerCategoryID;
	}
	
	public String getBillerDescription() {
		return billerDescription;
	}
	
	public void setBillerDescription(String billerDescription) {
		this.billerDescription = billerDescription;
	}
	
	public String getTransactionAmount() {
		return transactionAmount;
	}
	
	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	
	public String getTransactionDescription() {
		return transactionDescription;
	}
	
	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}
	
	public String getCardNumber() {
		return cardNumber;
	}
	
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	public String getChargeAmount() {
		return chargeAmount;
	}
	
	public void setChargeAmount(String chargeAmount) {
		this.chargeAmount = chargeAmount;
	}
	
	public String getTaxAmount() {
		return taxAmount;
	}
	
	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}
	
	
	
	
}