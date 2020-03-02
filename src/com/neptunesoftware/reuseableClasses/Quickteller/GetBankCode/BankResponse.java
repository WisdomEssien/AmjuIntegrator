package com.neptunesoftware.reuseableClasses.Quickteller.GetBankCode;

import java.util.List;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class BankResponse extends ResponseModel {

	private List<bank> banks;

	public BankResponse() {
		super();
	}
	
	public BankResponse(String responseCode, String resposonseMessage) {
		super(responseCode, resposonseMessage);
	}
	
	public BankResponse(List<bank> banks) {
		super();
		this.banks = banks;
	}

	public List<bank> getBanks() {
		return banks;
	}

	public void setBanks(List<bank> banks) {
		this.banks = banks;
	}
	
}
