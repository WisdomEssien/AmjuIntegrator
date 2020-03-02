package com.company.application.account;

import com.company.application.account.data.AccountHistoryResponse;
import com.company.application.account.data.AccountResponse;
import com.company.application.account.data.BeneficiaryRequest;
import com.company.application.account.data.BeneficiaryResponse;
import com.company.application.account.data.MiniStatementResponse;
import com.company.application.account.data.MultiAccountResponse;
import com.company.application.balanceEnquiry.BalanceEnquiryService;
import com.company.application.balanceEnquiry.data.BalanceEnquiryResponse;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;
import com.neptunesoftware.supernova.ws.client.FundsTransferWebService;
import com.neptunesoftware.supernova.ws.client.FundsTransferWebServiceEndPointPort_Impl;
import com.neptunesoftware.supernova.ws.server.transfer.data.NameInquiryRequestData;
import com.neptunesoftware.supernova.ws.server.transfer.data.NameInquiryResponseData;

public class AccountService {

	private String ipAddress;
	private String portNo;
	private String channelCode;
	private String transactionLimitInternal;
	private String transactionLimitExternal;
	
	public AccountService() {
		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();
		
		this.ipAddress = rubikonCredential.getIpAddress();
		this.portNo = rubikonCredential.getPortNumber();
		this.channelCode = rubikonCredential.getChannelCode();
		this.transactionLimitInternal = rubikonCredential.getTransferLimitInternal();
		this.transactionLimitExternal = rubikonCredential.getTransferLimitExternal();
	}
	
	
	public AccountResponse nameEnquiryRubikon(final String accountNo) {
		System.out.println("\n**** In Name Enquiry Rubikon ****");
		
		AccountResponse accountResponse = new AccountResponse();
		
		NameInquiryRequestData inquiryRequest = new NameInquiryRequestData();
		inquiryRequest.setSessionId(String.valueOf(System.currentTimeMillis()));
		inquiryRequest.setDestinationInstitutionCode("");
		inquiryRequest.setChannelCode(channelCode);
		inquiryRequest.setAccountNumber(accountNo);
		
		try {			
			
//			//ADDOSSER implementation
//			URL url = new URL("http://" + ipAddress + ":" + portNo + "/supernovaws/FundsTransferWebServiceEndPointPort?wsdl");
//			FundsTransferWebServiceStub accountWebServiceStub = new FundsTransferWebServiceEndPointPort(url).getFundsTransferWebServiceStubPort();
//			NameInquiryResponseData inquiryResponse = accountWebServiceStub.nameenquirysingleitem(inquiryRequest);

			//AMJU implementation
			FundsTransferWebService transferWebService = new FundsTransferWebServiceEndPointPort_Impl(
					"http://" + ipAddress + ":" + portNo + "/supernovaws/FundsTransferWebServiceBean?wsdl")
							.getFundsTransferWebServiceSoapPort("proxy_user".getBytes(),
									"proxy_password".getBytes());
			
			NameInquiryResponseData inquiryResponse = transferWebService.nameenquirysingleitem(inquiryRequest);
			
			// if customer name is not returned
			if (inquiryResponse.getAccountName() == null) {
				//accountResponse.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
				//accountResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
				
				accountResponse = nameEnquiryDatabase(accountNo);
				
				return accountResponse;
			}
			
			//success
			accountResponse.setAccountName(inquiryResponse.getAccountName());
			accountResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			accountResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
						
			System.out.println("responseCode: " +  inquiryResponse.getResponseCode());

			return accountResponse;

		} catch (Exception ex) {
			//accountResponse.setResponseCode(ResponseConstants.EXCEPTION_CODE);
			//accountResponse.setResponseMessage("Service endpoint not available.");
			
			accountResponse = nameEnquiryDatabase(accountNo);
			return accountResponse;
		}
	}
	
	private AccountResponse nameEnquiryDatabase(final String accountNo) {
		System.out.println("\n**** In Name Enquiry Database ****");

		AccountResponse accountResponse = new AccountResponse();
		String name = new AccountDBOperation().accountName(accountNo);
		
		// success
		accountResponse.setAccountName(name);
		accountResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		accountResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		// if customer name is not returned
		if (name == null) {
			accountResponse.setResponseCode(ResponseConstants.EXCEPTION_CODE);
			accountResponse.setResponseMessage(ResponseConstants.EXCEPTION_MESSAGE);
			
		} else if (name.isEmpty()) {
			accountResponse.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
			accountResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
		}

		return accountResponse;

	}
	
	public String accountName(final String accountNumber) {
		System.out.println("**Start getAccountName");
		
		AccountResponse accountResponse = nameEnquiryDatabase(accountNumber);
		if (!accountResponse.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE)) {
			System.out.println("Account Name: Could not get account name");
			System.out.println("**End getAccountName");
			return "";
		}
		
		System.out.println("Account Name : " + accountResponse.getAccountName());
		System.out.println("**End getAccountName");
		return accountResponse.getAccountName();			
	}
	
	public MultiAccountResponse multipleAccount(final String accountNo) {
		System.out.println("\n**** In MultiAccount Info ****");
		
		MultiAccountResponse multipleAcct = new MultiAccountResponse();

		AccountDBOperation dbConnection = new AccountDBOperation();
		multipleAcct.setAccounts(dbConnection.selectMultiAccount(accountNo));
			
		if(multipleAcct.getAccounts().equals(null)) {
			multipleAcct.setResponseCode(ResponseConstants.EXCEPTION_CODE);
			multipleAcct.setResponseMessage(ResponseConstants.EXCEPTION_MESSAGE);
			
			return multipleAcct;
		}

		// success
		multipleAcct.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		multipleAcct.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		// iterate through the list of accounts
		for (AccountResponse account : multipleAcct.getAccounts()) {

			// if account has no phone number
			if (account.getAccountNumber().equals(accountNo) && (account.getPhoneNumber().equalsIgnoreCase(""))) {
				multipleAcct.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
				multipleAcct.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE + " - Phone Number");
				return multipleAcct;
			}

			// calculating remaining transaction limit
			account.setDailyTransactionLimitInternal(transactionLimitInternal); // TransactionLimitInternal;
			account.setDailyTransactionDoneInternal(
					dbConnection.selectDailyTranxDone(account.getAccountNumber(), "InternalFundTransfer"));

			account.setDailyTransactionLimitExternal(transactionLimitExternal); // TransactionLimitExternal;
			account.setDailyTransactionDoneExternal(
					dbConnection.selectDailyTranxDone(account.getAccountNumber(), "ExternalFundTransfer"));

			// dailyTransactionRemaining = transactionLimitInternal - (internalTransferDone
			// + externalTransferDone)
			String dailyRemTrans = (Double.valueOf(transactionLimitInternal)
					- (Double.valueOf(account.getDailyTransactionDoneInternal())
							+ Double.valueOf(account.getDailyTransactionDoneExternal())))
					+ "";

			account.setDailyTransactionRemaining(dailyRemTrans);

			// idealExternalTransferRemaining = dailyTransactionRemaining -
			// (transactionLimitExternal - externalTransferDone)
			double idealExternalRemaining = (Double.valueOf(transactionLimitExternal)
					- Double.valueOf(account.getDailyTransactionDoneExternal()));
			account.setDailyTransactionRemainingInternal(account.getDailyTransactionRemaining());

			String dailyRemTransExt = (idealExternalRemaining > Double.valueOf(account.getDailyTransactionRemaining()))
					? account.getDailyTransactionRemaining()
					: idealExternalRemaining + "";

			account.setDailyTransactionRemainingExternal(dailyRemTransExt);

			// default all balances to empty
			account.setLedgerBalance("");

			// call balance enquiry to get account balance
			BalanceEnquiryResponse response = new BalanceEnquiryService()
					.balanceEnquiryRubikon(account.getAccountNumber());
			if (response.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE)) {
				account.setLedgerBalance(response.getAvailableBalance());
			}

		}

		// if account is empty, nothing was fetched cause account_no doesn't exist
		if (multipleAcct.getAccounts().isEmpty()) {
			multipleAcct.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
			multipleAcct.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
		}

		return multipleAcct;
		
	}
    
	public AccountResponse accountInfo(final String accountNo){
		System.out.println("\n**** In Account Info ****");
		
		AccountResponse accountResponse = new AccountResponse();
		
		AccountDBOperation dbConnection = new AccountDBOperation();		
		accountResponse = dbConnection.selectAccountInfo(accountNo);
		
		if (accountResponse.equals(null)) {
			
			CommonMethods.logSensitiveContent(" ** Account Info request ** \n" + accountNo + "\n\r\n\r"
					+ " ** Account Info response ** \n" + CommonMethods.ObjectToJsonString(new AccountResponse(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE)));
			
			return new AccountResponse(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE);
		}
		
		AccountResponse accountResponseDup = new AccountResponse();
		accountResponseDup.setAccountName(accountResponse.getAccountName());
		accountResponseDup.setAccountNumber(accountResponse.getAccountNumber());
		accountResponseDup.setAccountStatus(accountResponse.getAccountStatus());
		accountResponseDup.setAccountType(accountResponse.getAccountType());
		accountResponseDup.setLedgerBalance("******");
		accountResponseDup.setCurrencyCode(accountResponse.getCurrencyCode());
		accountResponseDup.setBankVerificationNumber("*****");
		accountResponseDup.setPhoneNumber(accountResponse.getPhoneNumber());
		accountResponseDup.setAccessPin("*****");
		
		CommonMethods.logSensitiveContent(" ** Account Info request ** \n" + accountNo + "\n\r\n\r"
				+ " ** Account Info response ** \n" + CommonMethods.ObjectToJsonString(accountResponseDup));
		
		return accountResponse;

	}
	
	public AccountHistoryResponse accountHistory(final String accountNo, String startDate, String endDate){
		System.out.println("\n**** In Account History ****");
		
		startDate = startDate.isEmpty() 
				? CommonMethods.getCurrentDateAsString("", "yyyy-MM-dd")
				: CommonMethods.addMonth(CommonMethods.getCurrentDateAsString(startDate, "yyyy-MM-dd"), "yyyy-MM-dd", 1);
		
		endDate = endDate.isEmpty() ? CommonMethods.getCurrentDateAsString("", "yyyy-MM-dd") : endDate;
		
		AccountHistoryResponse accountHistory = new AccountHistoryResponse();
		
		AccountDBOperation dbConnection = new AccountDBOperation();
		accountHistory.setAccountHistory(dbConnection.selectAccountHistory(accountNo, startDate, endDate));
		
		// success
		accountHistory.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		accountHistory.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		// if empty
		if (accountHistory.getAccountHistory().isEmpty()) {
			accountHistory.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
			accountHistory.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
			
		} else if (accountHistory.getAccountHistory().equals(null)) {
			accountHistory.setResponseCode(ResponseConstants.EXCEPTION_CODE);
			accountHistory.setResponseMessage(ResponseConstants.EXCEPTION_MESSAGE);
		}

		return accountHistory;

	}
	
	public MiniStatementResponse miniStatement(final String accountNo){
		System.out.println("\n**** In Mini Statement ****");
		MiniStatementResponse miniStatementResponse = new MiniStatementResponse();
		
		AccountDBOperation dbConnection = new AccountDBOperation();
		miniStatementResponse.setMiniStatement(dbConnection.selectMiniStatement(accountNo));
		
		// success
		miniStatementResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		miniStatementResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		if (miniStatementResponse.getMiniStatement().equals(null)) {
			miniStatementResponse.setResponseCode(ResponseConstants.EXCEPTION_CODE);
			miniStatementResponse.setResponseMessage(ResponseConstants.EXCEPTION_MESSAGE);
			
		}else if (miniStatementResponse.getMiniStatement().isEmpty()) {
			miniStatementResponse.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
			miniStatementResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
		}

		return miniStatementResponse;

	}

	public BeneficiaryResponse beneficiary(final String senderAcctNo, final String moduleId){
		System.out.println("\n****Beneficiary Enquiry ****");

		BeneficiaryResponse beneficiaryResponse = new BeneficiaryResponse();

		AccountDBOperation dbConnection = new AccountDBOperation();
		beneficiaryResponse.setBeneficiary(dbConnection.selectBeneficiary(senderAcctNo, moduleId));

		// success
		beneficiaryResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		beneficiaryResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		if (beneficiaryResponse.getBeneficiary().equals(null)) {
			beneficiaryResponse.setResponseCode(ResponseConstants.EXCEPTION_CODE);
			beneficiaryResponse.setResponseMessage(ResponseConstants.EXCEPTION_MESSAGE);

		} else if (beneficiaryResponse.getBeneficiary().isEmpty()) {
			beneficiaryResponse.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
			beneficiaryResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
		}

		return beneficiaryResponse;
	}
	
	public ResponseModel removeBeneficiary(final String senderAcctNo, final String moduleId, final int beneficiaryId){
		System.out.println("\n****Beneficiary Delete ****");

		AccountDBOperation dbConnection = new AccountDBOperation();

		ResponseModel responseModel = dbConnection.deleteBeneficiary(senderAcctNo, moduleId, beneficiaryId);

		return responseModel;
	}
	
	public ResponseModel saveBeneficiary(String body) {
		System.out.println("\n****Beneficiary Save ****");
		
		BeneficiaryRequest beneficiaryRequest = (BeneficiaryRequest) CommonMethods.JSONStringToObject(body, BeneficiaryRequest.class);
		
		AccountDBOperation dbConnection = new AccountDBOperation();
		
		ResponseModel responseModel = dbConnection.saveBeneficiary(beneficiaryRequest.getModuleId(), beneficiaryRequest.getSenderAcctNo(),
													beneficiaryRequest.getBeneficiaryAcctNo(), beneficiaryRequest.getBeneficiaryAcctName(),
													beneficiaryRequest.getBankCode(), beneficiaryRequest.getBankName());
		
		return responseModel;
	}

	
	
	
	
	public static void main(String[] args) {
		//AccountResponse  accountResponse = new AccountService().nameEnquiryDatabase("3002015994");
		//AccountResponse  accountResponse = new AccountService().nameEnquiryRubikon("3002015994");
		MultiAccountResponse  multiAccountResponse = new AccountService().multipleAccount("3002015994");
		
		System.out.println("Name: " + CommonMethods.ObjectToJsonString(multiAccountResponse));
	}
	
}
