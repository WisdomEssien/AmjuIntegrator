package com.company.application.cardManagement.data;

public class CardOwnedRequest {

	private String customerNumebr;
	private String accountNumber;
	
	public CardOwnedRequest(String customerNumber, String accountNumber) {
		this.customerNumebr = customerNumber;
		this.accountNumber = accountNumber;
	}
	
	public CardOwnedRequest() {}
	
	public String getCustomerNumebr() {
		return customerNumebr;
	}
	
	public void setCustomerNumebr(String customerNumebr) {
		this.customerNumebr = customerNumebr;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	
	
}
