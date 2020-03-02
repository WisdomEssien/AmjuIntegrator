package com.company.application.account.data;

public class MiniStatement {

	private String date;
	private String creditDebit;
	private String amount;
	private String refNo;
	
	public String getDate() {
		return date == null ? "" : date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getCreditDebit() {
		return creditDebit == null ? "" : creditDebit;
	}
	
	public void setCreditDebit(String creditDebit) {
		this.creditDebit = creditDebit;
	}
	
	public String getAmount() {
		return amount == null ? "" : amount;
	}
	
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getRefNo() {
		return refNo == null ? "" : refNo;
	}
	
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
	
	
}
