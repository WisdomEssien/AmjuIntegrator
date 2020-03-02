package com.company.application.cardManagement.data;

public class NewCardRequest {

	private String accountNumber;
	private String cardType;
	private String nameOnCard;
	
	public NewCardRequest() {}

	public NewCardRequest(String accountNumber, String cardType, String nameOnCard) {
		super();
		this.accountNumber = accountNumber;
		this.cardType = cardType;
		this.nameOnCard = nameOnCard;
	}

	public String getAccountNumber() {
		return accountNumber == null ? "" : accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getCardType() {
		return cardType == null ? "" : cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getNameOnCard() {
		return nameOnCard == null ? "" : nameOnCard;
	}

	public void setNameOnCard(String nameOnCard) {
		this.nameOnCard = nameOnCard;
	}
	
	
}
