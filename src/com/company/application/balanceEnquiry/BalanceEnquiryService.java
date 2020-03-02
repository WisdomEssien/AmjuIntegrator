package com.company.application.balanceEnquiry;

import java.math.BigDecimal;

import com.company.application.account.AccountService;
import com.company.application.balanceEnquiry.data.BalanceEnquiryResponse;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;
import com.neptunesoftware.supernova.ws.client.AccountWebService;
import com.neptunesoftware.supernova.ws.client.AccountWebServiceEndPointPort_Impl;
import com.neptunesoftware.supernova.ws.server.account.data.BalanceEnquiryRequestData;
import com.neptunesoftware.supernova.ws.server.account.data.BalanceEnquiryResponseData;

public class BalanceEnquiryService {
	
	private String ipAddress;
	private String portNo;
	private String channelCode;
	
	public BalanceEnquiryService() {
		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();
		
		this.ipAddress = rubikonCredential.getIpAddress();
		this.portNo = rubikonCredential.getPortNumber();
		this.channelCode = rubikonCredential.getChannelCode();
	}
	
	
	public BalanceEnquiryResponse balanceEnquiryRubikon(String accountNo) {
		System.out.println("\n**** In Balance Enquiry Rubikon ****");
		
		BalanceEnquiryResponse balanceEnquiryResponse = new BalanceEnquiryResponse();

		try {
			
			// Balance enquiry parameters
			BalanceEnquiryRequestData balEnqRequest = new BalanceEnquiryRequestData();
			balEnqRequest.setSessionId(String.valueOf(System.currentTimeMillis()));
			balEnqRequest.setDestinationInstitutionCode("");
			balEnqRequest.setChannelCode(channelCode);
			balEnqRequest.setAuthorizationCode("");
			balEnqRequest.setTargetAccountName("");
			balEnqRequest.setTargetBankVerificationNumber("");
			balEnqRequest.setTargetAccountNumber(accountNo);
			
			
//			// ADDOSSER implementation
//			URL url = new URL("http://" + ipAddress + ":" + portNo + "/supernovaws/AccountWebServiceEndPointPort?wsdl");
//			AccountWebServiceStub accountWebServiceStub = new AccountWebServiceEndPointPort(url).getAccountWebServiceStubPort();
//			BalanceEnquiryResponseData balEnqResponse = accountWebServiceStub.balanceenquiry(balEnqRequest);
			
			// AMJU implementation
			AccountWebService accountWebService = new AccountWebServiceEndPointPort_Impl(
						"http://" + ipAddress + ":" + portNo + "/supernovaws/AccountWebServiceBean?wsdl")
								.getAccountWebServiceSoapPort("proxy_user".getBytes(), "proxy_password".getBytes());
			
			BalanceEnquiryResponseData balEnqResponse = accountWebService.balanceenquiry(balEnqRequest);

			// if 00 is not returned as responseCode from balance inquiry, query balance from DB directly
			String status = balEnqResponse.getResponseCode() == null ? "-9990" : balEnqResponse.getResponseCode() + "";
			if (!status.equalsIgnoreCase(ResponseConstants.SUCCEESS_CODE)) {
				
				return balanceEnquiryDatabase(accountNo);				
				
			}
			
			// accountName
			String accountName = balEnqResponse.getTargetAccountName() == null 
									? "" : balEnqResponse.getTargetAccountName();
			
			// do a name enquiry to collect the name and bvn since
			// balance enquiry doesn't return the name and bvn
			if (accountName.isEmpty()) {
				accountName = new AccountService().accountName(accountNo);
			}

			balanceEnquiryResponse.setAccountName(accountName);
			balanceEnquiryResponse.setAccountNumber(accountNo);
			balanceEnquiryResponse.setAvailableBalance(balEnqResponse.getAvailableBalance() + "");
			balanceEnquiryResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			balanceEnquiryResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

			//System.out.println("balance: " + CommonMethods.ObjectToJsonString(balanceEnquiryResponse));
			
			return balanceEnquiryResponse;
			
		} catch (Exception ex) {
//			balanceEnquiryResponse.setResponseCode(ResponseConstants.EXCEPTION_CODE);
//			balanceEnquiryResponse.setResponseMessage(ResponseConstants.EXCEPTION_MESSAGE + "- Service endpoint not available.");
//
//			return balanceEnquiryResponse;
			
			return balanceEnquiryDatabase(accountNo);
		}
	}

	private BalanceEnquiryResponse balanceEnquiryDatabase(String accountNo) {
		System.out.println("\n**** In Balance Enquiry Database ****");
		
		// select account balance
		BalanceEnquiryDBOperation balanceEnquiryDBOperation = new BalanceEnquiryDBOperation();
		String balance = balanceEnquiryDBOperation.accountBalance(accountNo);
		
		// if null text is returned then account is not available
		if(balance.equalsIgnoreCase("null")) {
			BalanceEnquiryResponse response = new BalanceEnquiryResponse();
			response.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
			response.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
			
			System.out.println("balance: " + CommonMethods.ObjectToJsonString(response));
			
			return response;
		}

		// fetch account name
		String accountName = new AccountService().accountName(accountNo);
				
		// set response object
		BalanceEnquiryResponse response = new BalanceEnquiryResponse();
		response.setAccountName(accountName);
		response.setAccountNumber(accountNo);
		response.setAvailableBalance(balance);
		response.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		response.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		
		if(balance.isEmpty()) {
			response.setResponseCode(ResponseConstants.EXCEPTION_CODE);
			response.setResponseMessage(ResponseConstants.EXCEPTION_MESSAGE);
			
		}

		//System.out.println("balance: " + CommonMethods.ObjectToJsonString(response));
		
		return response;
	}
	
	public boolean hasSufficientFunds(String accountNumber, double transactionAmount){
		System.out.println("**Start HasSufficientFunds");
				
		//call balance enquiry to get account balance				 
		BalanceEnquiryResponse balanceResponse = balanceEnquiryRubikon(accountNumber);
		if (!balanceResponse.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE)) {
			System.out.println("Could not verify account balance");
			System.out.println("**End HasSufficientFunds");
			return false;
        }
		
		//return insufficient funds when transaction amount is less than available balance
		BigDecimal availableBalance = new BigDecimal(balanceResponse.getAvailableBalance());
		if(availableBalance.compareTo(new BigDecimal(transactionAmount)) < 0) {
			
			System.out.println("Available balance is less than Transaction Amount.");
			System.out.println("**End HasSufficientFunds");
			return false;
		}

		System.out.println("success");
		System.out.println("**End HasSufficientFunds");
		return true;
	}
	
	public static void main(String[] args) {
		BalanceEnquiryResponse  balanceEnquiryResponse = new BalanceEnquiryService().balanceEnquiryRubikon("3004000018");
		
		System.out.println("balance: " + CommonMethods.ObjectToJsonString(balanceEnquiryResponse));
		//System.out.println("balance: " + balanceEnquiryResponse.getAvailableBalance());
	}
	
}
