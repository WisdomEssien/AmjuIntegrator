package com.company.application.newCustomer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.company.application.newCustomer.data.CustomerData;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.Database.DBConnection;

public class NewCustomerDBOperation extends DBConnection{

	public NewCustomerDBOperation() {
		super();
	}
	
	public NewCustomerDBOperation(final String databaseName) {
		super(databaseName);
	}
		
	public NewCustomerDBOperation(final String driver, final String connectionURL, final String username, final String password, final String databaseType) {
		super(driver, connectionURL, username, password, databaseType);
	}
	
	
	public CustomerData getCustomerInfo(final String accountNumber) {
		
		Connection dbConnection = null;
		dbConnection = databaseConnection();
			
		PreparedStatement pst = null;
		
		ResultSet rs = null;
		
		try {
			
			String query = "select cust_id, cust_no, cust_nm from customer \r\n" + 
					"where cust_id = (select cust_id from account where acct_no = ?)";
			
			pst = dbConnection.prepareStatement(query);
			
			pst.setString(1, accountNumber);
			
			rs = pst.executeQuery();
			
			CustomerData customerData = new CustomerData();
			customerData.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
			customerData.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
			
			while (rs.next()) {
				customerData.setCustomerId(rs.getString(1));
				customerData.setCustomerNumber(rs.getString(2));
				customerData.setCustomerName(rs.getString(3));
								
				customerData.setResponseCode(ResponseConstants.SUCCEESS_CODE);
				customerData.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
			}
			
			return customerData;
		} catch (Exception e) {
			System.out.println("Select failed");
			return null;
		} finally {
			try {
				if (dbConnection != null) {
					dbConnection.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
			}
		}
		
	}
	
	
	
}
