package com.company.application.newCustomer.data;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class CustomerData extends ResponseModel{

	private String customerName;
	private String customerNumber;
	private String customerId;
	
	public CustomerData() {}
	
	public CustomerData(String responseCode, String responseMessage) {
		super(responseCode, responseMessage);
	}
	
	public String getCustomerName() {
		return customerName;
	}
	
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public String getCustomerNumber() {
		return customerNumber;
	}
	
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	
	public String getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	
}
