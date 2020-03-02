package com.company.application.fundTransfer;

import com.neptunesoftware.reuseableClasses.Database.DBConnection;

public class FundTransferDBOperation extends DBConnection {

	public FundTransferDBOperation() {
		super();
	}
	
	public FundTransferDBOperation(String databaseName) {
		super(databaseName);
	}
	
	public FundTransferDBOperation(String driver, String connectionURL, String username, String password, String databaseType) {
		super(driver, connectionURL, username, password, databaseType);
	}

	
	

	
	
}
