package com.company.application.cardManagement.data;

public class CardHotlistRequest {

	private String accountNumber;
	private String cardPan;
	
	public CardHotlistRequest() {}
	
	public CardHotlistRequest(String accountNumber, String cardPan) {
		super();
		this.accountNumber = accountNumber;
		this.cardPan = cardPan;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getCardPan() {
		return cardPan;
	}

	public void setCardPan(String cardPan) {
		this.cardPan = cardPan;
	}

	
	
}
