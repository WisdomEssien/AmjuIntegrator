package com.neptunesoftware.reuseableClasses.Quickteller.GetBankCode;

public class bank {

	private String id;
	private String cbnCode;
	private String bankName;
	private String bankCode;
	
	public bank() {super();}
	
	public bank(String id, String cbnCode, String bankName, String bankCode) {
		super();
		this.id = id;
		this.cbnCode = cbnCode;
		this.bankName = bankName;
		this.bankCode = bankCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCbnCode() {
		return cbnCode;
	}

	public void setCbnCode(String cbnCode) {
		this.cbnCode = cbnCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	
	
}
