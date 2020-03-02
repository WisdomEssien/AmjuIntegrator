package com.company.application.account.data;

import java.util.ArrayList;
import java.util.List;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class AccountHistoryResponse extends ResponseModel{
	private List<AccountHistory> accountHistory = new ArrayList<AccountHistory>();


	public AccountHistoryResponse() {
		
	}
	
	public AccountHistoryResponse(List<AccountHistory> accountHistory) {
		this.accountHistory = accountHistory;
	}	
	
	public List<AccountHistory> getAccountHistory() {
		return accountHistory;
	}

	public void setAccountHistory(List<AccountHistory> accountHistory) {
		this.accountHistory = accountHistory;
	}
		
}
