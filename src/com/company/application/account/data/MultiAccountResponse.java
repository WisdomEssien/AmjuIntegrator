package com.company.application.account.data;

import java.util.List;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class MultiAccountResponse extends ResponseModel{

	private List<AccountResponse> accounts;

	public List<AccountResponse> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountResponse> accounts) {
		this.accounts = accounts;
	}
	
	
}
