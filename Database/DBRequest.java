package com.neptunesoftware.reuseableClasses.Database;

public class DBRequest {
	private String senderAcctNo;
	private String amount;
	private String contraAmount;
	private String beneficiaryAcctNo;
	private String narration;	
	private String chargeAmount;
	private String taxAmount;	
	private String initiatingApp;
	
	public DBRequest() {}
	
	public DBRequest(String senderAcctNo, String amount, String contraAmount, String beneficiaryAcctNo, String narration,
			String chargeAmount, String taxAmount, String initiatingApp){
		this.senderAcctNo = senderAcctNo;
		this.amount = amount;
		this.contraAmount = contraAmount;
		this.beneficiaryAcctNo = beneficiaryAcctNo;
		this.narration = narration;	
		this.chargeAmount = chargeAmount;
		this.taxAmount = taxAmount;	
		this.initiatingApp = initiatingApp;
	}

	public String getSenderAcctNo() {
		return senderAcctNo == null ? "" : senderAcctNo;
	}

	public void setSenderAcctNo(String senderAcctNo) {
		this.senderAcctNo = senderAcctNo;
	}

	public String getAmount() {
		return amount == null ? "" : amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getContraAmount() {
		return contraAmount == null ? "" : contraAmount;
	}

	public void setContraAmount(String contraAmount) {
		this.contraAmount = contraAmount;
	}

	public String getBeneficiaryAcctNo() {
		return beneficiaryAcctNo == null ? "" : beneficiaryAcctNo;
	}

	public void setBeneficiaryAcctNo(String beneficiaryAcctNo) {
		this.beneficiaryAcctNo = beneficiaryAcctNo;
	}

	public String getNarration() {
		return narration == null ? "" : narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
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
