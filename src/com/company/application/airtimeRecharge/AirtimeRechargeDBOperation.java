package com.company.application.airtimeRecharge;

import com.neptunesoftware.reuseableClasses.Database.DBConnection;

public class AirtimeRechargeDBOperation extends DBConnection{

	public AirtimeRechargeDBOperation() {
		super("Oracle");
	}
	
	public AirtimeRechargeDBOperation(String databaseName) {
		super(databaseName);
	}
		
	public AirtimeRechargeDBOperation(String driver, String connectionURL, String username, String password, String databaseType) {
		super(driver, connectionURL, username, password, databaseType);
	}
	
	
	
	
}
