package com.company.application.account.data;

public class AccountHistory {

	private String accountType;
	private String transactionDesc;
	private String transactionAmount;
	private String balanceAfter;
	private String transactionDate;
	
	public String getAccountType() {
		return accountType == null ? "" : accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getTransactionDesc() {
		return transactionDesc == null ? "" : transactionDesc;
	}
	public void setTransactionDesc(String transactionDesc) {
		this.transactionDesc = transactionDesc;
	}
	public String getTransactionAmount() {
		return transactionAmount == null ? "" : transactionAmount;
	}
	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public String getBalanceAfter() {
		return balanceAfter == null ? "" : balanceAfter;
	}
	public void setBalanceAfter(String balanceAfter) {
		this.balanceAfter = balanceAfter;
	}
	public String getTransactionDate() {
		return transactionDate == null ? "" : transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	
}
