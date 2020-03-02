package com.company.application.newCustomer;

import com.company.application.newCustomer.data.CustomerData;
import com.neptunesoftware.reuseableClasses.ResponseConstants;

public class NewCustomerService {

	public CustomerData customerInfo(String accountNumber) {
		
		NewCustomerDBOperation database = new NewCustomerDBOperation();
		
		CustomerData customerData = database.getCustomerInfo(accountNumber);
		
		if (customerData == null) {			
			return new CustomerData(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE);
		}
		
		System.out.println("CustomerNumber: " + customerData.getCustomerNumber());
		return customerData;
	}
	
	public static void main(String[] a) {
		NewCustomerService customerService = new NewCustomerService();
		customerService.customerInfo("3000157994");
	}
	
}
