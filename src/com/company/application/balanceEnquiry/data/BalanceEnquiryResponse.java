package com.company.application.balanceEnquiry.data;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class BalanceEnquiryResponse extends ResponseModel{
	
	private String accountName;
	private String accountNumber;
	private String availableBalance;
	
	public BalanceEnquiryResponse() {
	}
	
	public BalanceEnquiryResponse(String accountName, String accountNumber, String availableBalance) {
		super();
		this.accountName = accountName;
		this.accountNumber = accountNumber;
		this.availableBalance = availableBalance;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(String availableBalance) {
		this.availableBalance = availableBalance;
	}

	

}
