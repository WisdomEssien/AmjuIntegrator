package com.neptunesoftware.reuseableClasses.Quickteller.QueryTransaction;

public class QueryTransactionResponse {

	private Biller billPayment;
	private Biller recharge;
	private FundsTransfer fundsTransfer;
	private String amount;
	private String currencyCode;
	private String customer;
	private String customerEmail;
	private String customerMobile;
	private String paymentDate;
	private String requestReference;
	private String serviceCode;
	private String serviceName;
	private String serviceProviderId;
	private String status;
	private String surcharge;
	private String transactionRef;
	private String transactionResponseCode;
	private String transactionSet;
	private String responseCode;
	
	public QueryTransactionResponse() {
	}
	
	public QueryTransactionResponse(Biller billPayment, Biller recharge, String amount, String currencyCode,
			String customer, String customerEmail, String customerMobile, String paymentDate, String requestReference,
			String serviceCode, String serviceName, String serviceProviderId, String status, String surcharge,
			String transactionRef, String transactionResponseCode, String transactionSet, String responseCode) {
		super();
		this.billPayment = billPayment;
		this.recharge = recharge;
		this.amount = amount;
		this.currencyCode = currencyCode;
		this.customer = customer;
		this.customerEmail = customerEmail;
		this.customerMobile = customerMobile;
		this.paymentDate = paymentDate;
		this.requestReference = requestReference;
		this.serviceCode = serviceCode;
		this.serviceName = serviceName;
		this.serviceProviderId = serviceProviderId;
		this.status = status;
		this.surcharge = surcharge;
		this.transactionRef = transactionRef;
		this.transactionResponseCode = transactionResponseCode;
		this.transactionSet = transactionSet;
		this.responseCode = responseCode;
	}

	public Biller getBillPayment() {
		return billPayment;
	}

	public void setBillPayment(Biller billPayment) {
		this.billPayment = billPayment;
	}

	public Biller getRecharge() {
		return recharge;
	}

	public void setRecharge(Biller recharge) {
		this.recharge = recharge;
	}

	public FundsTransfer getFundsTransfer() {
		return fundsTransfer;
	}

	public void setFundsTransfer(FundsTransfer fundsTransfer) {
		this.fundsTransfer = fundsTransfer;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getRequestReference() {
		return requestReference;
	}

	public void setRequestReference(String requestReference) {
		this.requestReference = requestReference;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSurcharge() {
		return surcharge;
	}

	public void setSurcharge(String surcharge) {
		this.surcharge = surcharge;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getTransactionResponseCode() {
		return transactionResponseCode == null ? "" : transactionResponseCode;
	}

	public void setTransactionResponseCode(String transactionResponseCode) {
		this.transactionResponseCode = transactionResponseCode;
	}

	public String getTransactionSet() {
		return transactionSet;
	}

	public void setTransactionSet(String transactionSet) {
		this.transactionSet = transactionSet;
	}

	public String getResponseCode() {
		return responseCode == null ? "" : responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
    
	
}


