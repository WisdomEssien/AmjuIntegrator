package com.company.application.account.data;

import java.util.List;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class MiniStatementResponse extends ResponseModel {
	
	private List<MiniStatement> miniStatement;
	
	public MiniStatementResponse() {}
	
	public MiniStatementResponse(List<MiniStatement> miniStatement) {
		this.miniStatement = miniStatement;
	}
	
	public List<MiniStatement> getMiniStatement() {
		return miniStatement;
	}

	public void setMiniStatement(List<MiniStatement> miniStatement) {
		this.miniStatement = miniStatement;
	}
	
	
}
