package com.company.application.account.data;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class AccountResponse extends ResponseModel{

	private String accountName;
	private String accountNumber;
	private String accountStatus;
	private String accountType;
	private String ledgerBalance;
	private String currencyCode;
	private String bankVerificationNumber;
	private String phoneNumber;
	private String accessPin;
	private String dailyTransactionLimitInternal;
	private String dailyTransactionDoneInternal;
	private String dailyTransactionRemainingInternal;
	private String dailyTransactionLimitExternal;
	private String dailyTransactionDoneExternal;
	private String dailyTransactionRemainingExternal;
	private String dailyTransactionRemaining;
	
	
	public AccountResponse() {}
	
	public AccountResponse(String responseCode, String responseMessage) {
		super(responseCode, responseMessage);
	}


	public String getAccountName() {
		return accountName == null ? "" : accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public String getAccountNumber() {
		return accountNumber == null ? "" : accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountStatus() {
		return accountStatus == null ? "" : accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getAccountType() {
		return accountType == null ? "" : accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getLedgerBalance() {
		return ledgerBalance == null ? "" : ledgerBalance;
	}

	public void setLedgerBalance(String ledgerBalance) {
		this.ledgerBalance = ledgerBalance;
	}

	public String getCurrencyCode() {
		return currencyCode == null ? "" : currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getBankVerificationNumber() {
		return bankVerificationNumber == null ? "" : bankVerificationNumber;
	}

	public void setBankVerificationNumber(String bankVerificationNumber) {
		this.bankVerificationNumber = bankVerificationNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber == null ? "" : phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAccessPin() {
		return accessPin == null ? "" : accessPin;
	}

	public void setAccessPin(String accessPin) {
		this.accessPin = accessPin;
	}

	public String getDailyTransactionLimitInternal() {
		return dailyTransactionLimitInternal == null ? "" : dailyTransactionLimitInternal;
	}

	public void setDailyTransactionLimitInternal(String dailyTransactionLimitInternal) {
		this.dailyTransactionLimitInternal = dailyTransactionLimitInternal;
	}

	public String getDailyTransactionDoneInternal() {
		return dailyTransactionDoneInternal == null ? "" : dailyTransactionDoneInternal;
	}

	public void setDailyTransactionDoneInternal(String dailyTransactionDoneInternal) {
		this.dailyTransactionDoneInternal = dailyTransactionDoneInternal;
	}

	public String getDailyTransactionRemainingInternal() {
		return dailyTransactionRemainingInternal == null ? "" : dailyTransactionRemainingInternal;
	}

	public void setDailyTransactionRemainingInternal(String dailyTransactionRemainingInternal) {
		this.dailyTransactionRemainingInternal = dailyTransactionRemainingInternal;
	}

	public String getDailyTransactionLimitExternal() {
		return dailyTransactionLimitExternal == null ? "" : dailyTransactionLimitExternal;
	}

	public void setDailyTransactionLimitExternal(String dailyTransactionLimitExternal) {
		this.dailyTransactionLimitExternal = dailyTransactionLimitExternal;
	}

	public String getDailyTransactionDoneExternal() {
		return dailyTransactionDoneExternal == null ? "" : dailyTransactionDoneExternal;
	}

	public void setDailyTransactionDoneExternal(String dailyTransactionDoneExternal) {
		this.dailyTransactionDoneExternal = dailyTransactionDoneExternal;
	}

	public String getDailyTransactionRemainingExternal() {
		return dailyTransactionRemainingExternal == null ? "" : dailyTransactionRemainingExternal;
	}

	public void setDailyTransactionRemainingExternal(String dailyTransactionRemainingExternal) {
		this.dailyTransactionRemainingExternal = dailyTransactionRemainingExternal;
	}

	public String getDailyTransactionRemaining() {
		return dailyTransactionRemaining == null ? "" : dailyTransactionRemaining;
	}

	public void setDailyTransactionRemaining(String dailyTransactionRemaining) {
		this.dailyTransactionRemaining = dailyTransactionRemaining;
	}
	
	
	
}
