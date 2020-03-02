package com.company.application.fundTransfer.data;

public class ExternalFTRequest {
	
	private String initiatingApp;
	private String fromAccountNumber;
	private String beneficiaryBankID;
	private String beneficiaryAccountNumber;
	private String beneficiaryName;
	private String transactionAmount;
	private String transactionDescription;
	private String transactionFee;	
	private String chargeAmount;
	private String taxAmount;
	
	
	public String getInitiatingApp() {
		return initiatingApp;
	}
	public void setInitiatingApp(String initiatingApp) {
		this.initiatingApp = initiatingApp;
	}
	public String getFromAccountNumber() {
		return fromAccountNumber;
	}
	public void setFromAccountNumber(String fromAccountNumber) {
		this.fromAccountNumber = fromAccountNumber;
	}
	public String getBeneficiaryBankID() {
		return beneficiaryBankID;
	}
	public void setBeneficiaryBankID(String beneficiaryBankID) {
		this.beneficiaryBankID = beneficiaryBankID;
	}
	public String getBeneficiaryAccountNumber() {
		return beneficiaryAccountNumber;
	}
	public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
		this.beneficiaryAccountNumber = beneficiaryAccountNumber;
	}
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
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
	public String getTransactionFee() {
		return transactionFee;
	}
	public void setTransactionFee(String transactionFee) {
		this.transactionFee = transactionFee;
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