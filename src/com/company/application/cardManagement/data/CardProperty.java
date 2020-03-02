package com.company.application.cardManagement.data;

public class CardProperty {

	private String cardPan;
	private String expiryDate;
	private String cardType;
	private String nameOnCard;
	private boolean cardBlocked;
	
	public CardProperty() {}

	public CardProperty(String cardPan, String expiryDate, String cardType, String nameOnCard) {
		super();
		this.cardPan = cardPan;
		this.expiryDate = expiryDate;
		this.cardType = cardType;
		this.nameOnCard = nameOnCard;
	}

	public String getCardPan() {
		return cardPan;
	}

	public void setCardPan(String cardPan) {
		this.cardPan = cardPan;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getNameOnCard() {
		return nameOnCard;
	}

	public void setNameOnCard(String nameOnCard) {
		this.nameOnCard = nameOnCard;
	}

	public boolean isCardBlocked() {
		return cardBlocked;
	}

	public void setCardBlocked(boolean cardBlocked) {
		this.cardBlocked = cardBlocked;
	}
	
	
	
}
