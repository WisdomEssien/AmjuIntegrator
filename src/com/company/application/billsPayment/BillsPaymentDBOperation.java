package com.company.application.billsPayment;

import com.neptunesoftware.reuseableClasses.Database.DBConnection;

public class BillsPaymentDBOperation extends DBConnection{

	public BillsPaymentDBOperation() {
		super();
	}
	
	public BillsPaymentDBOperation(String databaseName) {
		super(databaseName);
	}
		
	public BillsPaymentDBOperation(String driver, String connectionURL, String username, String password, String databaseType) {
		super(driver, connectionURL, username, password, databaseType);
	}
	
	
	
	
	
}
