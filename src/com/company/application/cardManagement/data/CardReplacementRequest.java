package com.company.application.cardManagement.data;

public class CardReplacementRequest extends CardHotlistRequest{

	private String deliveryType;
	private String deliveryAddress;
	
	public CardReplacementRequest() {
		super();
	}
	
	public CardReplacementRequest(String accountNumber, String cardPan, String deliveryType, String deliveryAddress) {
		super(accountNumber, cardPan);
		this.deliveryType = deliveryType;
		this.deliveryAddress = deliveryAddress;
	}
	
	
	public String getDeliveryType() {
		return deliveryType;
	}
	
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	
	
	
}
