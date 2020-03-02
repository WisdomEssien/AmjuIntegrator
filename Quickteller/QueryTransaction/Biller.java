package com.neptunesoftware.reuseableClasses.Quickteller.QueryTransaction;

public class Biller {
	
	private String biller;
	private String customerId1;
	private String customerId2;
	private String paymentTypeName;
	private String paymentTypeCode;
	private String billerId;
	
	public Biller() {}
	
	public Biller(String biller, String customerId1, String customerId2, String paymentTypeName, String paymentTypeCode,
			String billerId) {
		super();
		this.biller = biller;
		this.customerId1 = customerId1;
		this.customerId2 = customerId2;
		this.paymentTypeName = paymentTypeName;
		this.paymentTypeCode = paymentTypeCode;
		this.billerId = billerId;
	}

	public String getBiller() {
		return biller;
	}

	public void setBiller(String biller) {
		this.biller = biller;
	}

	public String getCustomerId1() {
		return customerId1;
	}

	public void setCustomerId1(String customerId1) {
		this.customerId1 = customerId1;
	}

	public String getCustomerId2() {
		return customerId2;
	}

	public void setCustomerId2(String customerId2) {
		this.customerId2 = customerId2;
	}

	public String getPaymentTypeName() {
		return paymentTypeName;
	}

	public void setPaymentTypeName(String paymentTypeName) {
		this.paymentTypeName = paymentTypeName;
	}

	public String getPaymentTypeCode() {
		return paymentTypeCode;
	}

	public void setPaymentTypeCode(String paymentTypeCode) {
		this.paymentTypeCode = paymentTypeCode;
	}

	public String getBillerId() {
		return billerId;
	}

	public void setBillerId(String billerId) {
		this.billerId = billerId;
	}
	
	
	
}
