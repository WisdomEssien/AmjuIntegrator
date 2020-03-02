package com.company.application.cardManagement.data;

import java.util.List;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class CardOwnedResponse extends ResponseModel {

	private List<CardProperty> cards;
	private boolean canRequestForCard;

	public CardOwnedResponse() {
		super();
	}
	
	public CardOwnedResponse(List<CardProperty> cards, String responseCode, String responseMessage) {
		super(responseCode, responseMessage);
		this.cards = cards;
	}

	public List<CardProperty> getCards() {
		return cards;
	}

	public void setCards(List<CardProperty> cards) {
		this.cards = cards;
	}

	public boolean isCanRequestForCard() {
		return canRequestForCard;
	}

	public void setCanRequestForCard(boolean canRequestForCard) {
		this.canRequestForCard = canRequestForCard;
	}
	
	
}
