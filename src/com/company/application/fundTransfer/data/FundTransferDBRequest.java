package com.company.application.fundTransfer.data;

import com.neptunesoftware.reuseableClasses.Database.DBRequest;

public class FundTransferDBRequest extends DBRequest {
	
	public FundTransferDBRequest() {}
	
	public FundTransferDBRequest(String senderAcctNo, String amount, String contraAmount, String beneficiaryAcctNo, String narration,
			String chargeAmount, String taxAmount, String initiatingApp){
		
		super(senderAcctNo, amount, contraAmount, beneficiaryAcctNo, narration, chargeAmount, taxAmount, initiatingApp);
	}
	
}
