package com.company.application.fundTransfer;

import java.util.Date;

import com.company.application.account.AccountService;
import com.company.application.balanceEnquiry.BalanceEnquiryService;
import com.company.application.fundTransfer.data.ExternalFTRequest;
import com.company.application.fundTransfer.data.FundTransferDBRequest;
import com.company.application.fundTransfer.data.InternalFTRequest;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Database.DBConnection;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.QuicktellerConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.FundTransferResponse;
import com.neptunesoftware.reuseableClasses.Quickteller.QueryTransaction.QueryTransactionResponse;
import com.neptunesoftware.reuseableClasses.Quickteller.data.Errors;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;


public class FundTransferService {

	private String channelCode;
	private String currencyCode;
	private String fundXferDebit;
	
	
	public FundTransferService() {
		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();
		
		this.channelCode = rubikonCredential.getChannelCode();
		this.currencyCode = rubikonCredential.getCurrencyCode();
		this.fundXferDebit = rubikonCredential.getFundTransferDebit();
	}
	
	
	/*
	 * This Method works with the assumption that transaction amount received is in kobo
	 */
	public String InternalFundTransfer(final String body) {
		System.out.println("\n**** Start Internal Transfer ****");

		InternalFTRequest internalTransfer = (InternalFTRequest) CommonMethods.JSONStringToObject(body, InternalFTRequest.class);
				
		FundTransferDBOperation database = new FundTransferDBOperation();

		
		double tran_Amount = CommonMethods.koboToNaira(Integer.parseInt(internalTransfer.getTransactionAmount().trim()));

		// check to make sure account has sufficient balance
		if (!new BalanceEnquiryService().hasSufficientFunds(internalTransfer.getFromAccountNumber(), tran_Amount)) {

			// return insufficient funds when transaction amount is less than available
			// balance
			ResponseModel responseModel = new ResponseModel();
			responseModel.setResponseCode(ResponseConstants.INSUFFICIENT_CODE);
			responseModel.setResponseMessage(ResponseConstants.INSUFFICIENT_MESSAGE);

			return CommonMethods.ObjectToJsonString(responseModel);
		}

		// call procedure to do deduction
		System.out.println("In Internal Fund Transfer Debit Sender And Credit Receiver");

		String narration = "Internal Transfer from #acctno " + internalTransfer.getFromAccountNumber() + " to #acctno "
				+ internalTransfer.getToAccountNumber();
		narration = narration + "\nSub Desc: " + internalTransfer.getTransactionDescription();

		FundTransferDBRequest dbRequest = new FundTransferDBRequest(internalTransfer.getFromAccountNumber(),
				tran_Amount+"", tran_Amount+"", internalTransfer.getToAccountNumber(), narration,
				internalTransfer.getChargeAmount(), internalTransfer.getTaxAmount(),
				internalTransfer.getInitiatingApp());

		String dbResponse = database.callProcedure(dbRequest, "", fundXferDebit, "", "N", "");

		ResponseModel responseModel = new ResponseModel();
		String response = "";

		// return error if deduction was not possible
		if (dbResponse == null || !dbResponse.equals("00")) {

			responseModel.setResponseCode(ResponseConstants.PROCEDURE_CODE);
			responseModel.setResponseMessage(ResponseConstants.PROCEDURE_MESSAGE);

			response = CommonMethods.ObjectToJsonString(responseModel);

			// log request and responses
			CommonMethods.logContent("*** InternalFundsTransfer Request ***" + body + "\r\n\r\n"
					+ "*** InternalFundsTransfer Response ***" + response);

			return response;
		}

		// success
		responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		response = CommonMethods.ObjectToJsonString(responseModel);

		
		String transactionRef = new QuicktellerConstants().getTransferCodePrefix() + "|" + new Date() + "|"
				+ new Date().getTime();
		try {
			
			// save to the db
			database.saveRecord(dbRequest, dbResponse, transactionRef, "InternalFundTransfer", "DR", channelCode,
					currencyCode);
		} catch (Exception e) {
			System.out.println("Failed to save record");
		}

		// log request and responses
		CommonMethods.logContent("*** InternalFundsTransfer Request ***" + body + "\r\n\r\n"
				+ "*** InternalFundsTransfer Response ***" + response);

		System.out.println("**** End Internal Transfer");
		return response;

	}
	
	
	/*
	 * This Method works with the assumption that transaction amount received is in kobo
	 */
	
	public String ExternalFundTransfer(final String body) {
		System.out.println("\n**** Start ExternalTransfers ****");
		
		ExternalFTRequest externalTransfer = (ExternalFTRequest) CommonMethods.JSONStringToObject(body, ExternalFTRequest.class);
		
		FundTransferDBOperation database = new FundTransferDBOperation();
		
		// to get sender's accountName
		String senderName = "";
		senderName = new AccountService().accountName(externalTransfer.getFromAccountNumber());

		// convert transaction amount to Naira
		int amountInKobo = Integer.parseInt(externalTransfer.getTransactionAmount().trim());
		double amountInNaira = CommonMethods.koboToNaira(amountInKobo);
		double charge_Amount = CommonMethods.koboToNaira(Double.parseDouble(externalTransfer.getChargeAmount().trim()));
		double tax_Amount = CommonMethods.koboToNaira(Double.parseDouble(externalTransfer.getTaxAmount().trim()));
				
		// total amount
		double charges = Double.sum(charge_Amount, tax_Amount);
		double totalAmount = Double.sum(amountInNaira, charges);
				
		//System.out.println("Total amount to be deducted: " + totalAmount);
		
		// check to make sure account has sufficient balance
		if (!new BalanceEnquiryService().hasSufficientFunds(externalTransfer.getFromAccountNumber(), totalAmount)) {
			
			// return insufficient funds when transaction amount is less than available balance
			ResponseModel responseModel = new ResponseModel();
			responseModel.setResponseCode(ResponseConstants.INSUFFICIENT_CODE);
			responseModel.setResponseMessage(ResponseConstants.INSUFFICIENT_MESSAGE);
			
			String response = CommonMethods.ObjectToJsonString(responseModel);
			
			// log request and responses
			CommonMethods.logContent("*** ExternalFundsTransfer Request ***" + body + "\r\n\r\n"
					+ "*** ExternalFundsTransfer Request(Interswitch) *** \r\n\r\n"
					+ "*** ExternalFundsTransfer Response(Interswitch) *** \r\n\r\n"
					+ "*** QueryTransaction Response *** \r\n\r\n"
					+ "*** ExternalFundsTransfer Response ***" + response);
						
			return response;
		}

		// call procedure to deduct from the senders account
		System.out.println("\n\n Debit Customer");
		if (!database.isTransactionSuccessful(senderName, externalTransfer, false, "", "")) {
						
			// return error if deduction was not possible
			ResponseModel responseModel = new ResponseModel();
			responseModel.setResponseCode(ResponseConstants.PROCEDURE_CODE);
			responseModel.setResponseMessage(ResponseConstants.PROCEDURE_MESSAGE);
			
			String response = CommonMethods.ObjectToJsonString(responseModel);
			
			// log request and responses
			CommonMethods.logContent("*** ExternalFundsTransfer Request ***" + body + "\r\n\r\n"
					+ "*** ExternalFundsTransfer Request(Interswitch) *** \r\n\r\n"
					+ "*** ExternalFundsTransfer Response(Interswitch) *** \r\n\r\n"
					+ "*** QueryTransaction Response *** \r\n\r\n"
					+ "*** ExternalFundsTransfer Response ***" + response);
						
			return response;
		}

		// deduction was successful proceed
		
		// call INTERSWITCH to carry-out transfer
		Quickteller quickteller = new Quickteller();
		String fundTransferResponseStr = quickteller.fundTransfer(externalTransfer.getBeneficiaryAccountNumber(), externalTransfer.getBeneficiaryName(), 
				String.valueOf(amountInKobo), externalTransfer.getBeneficiaryBankID(), senderName);
		
		//Deserialize response string to FundTransferResponse object
		FundTransferResponse fundTransferResponse =  (FundTransferResponse)CommonMethods.JSONStringToObject(fundTransferResponseStr, FundTransferResponse.class);
		
		//Also deserialize response string to Error object incase error object was returned not FundTransferResponse
		Errors errorResp = (Errors) CommonMethods.JSONStringToObject(fundTransferResponseStr, Errors.class);
		
		ResponseModel responseModel = new ResponseModel();
		
		// Return when neither FundTransferResponse nor Error object was returned
		if(fundTransferResponseStr.equals(null) || fundTransferResponseStr.isEmpty()) {
			
//			// call procedure to do a reversal (credit)
//			System.out.println("\n\n Credit Customer");
//			database.isTransactionSuccessful(senderName, externalTransfer, true, ResponseConstants.WEBSERVICE_UNAVAILABLE_CODE, "");
			
			//save reversed transaction
			new DBConnection().saveSupposedReversal(externalTransfer.getFromAccountNumber(), 
					externalTransfer.getBeneficiaryAccountNumber(), 
					CommonMethods.koboToNaira(Integer.parseInt(externalTransfer.getTransactionAmount().trim())),
					ResponseConstants.WEBSERVICE_UNAVAILABLE_CODE,
					ResponseConstants.WEBSERVICE_UNAVAILABLE_MESSAGE, 
					"ExternalFundTransfer", 
					CommonMethods.koboToNaira(Double.parseDouble(externalTransfer.getChargeAmount().trim())),
					CommonMethods.koboToNaira(Double.parseDouble(externalTransfer.getTaxAmount().trim())));
			
			
			// and return if it is an error object
			responseModel.setResponseCode(ResponseConstants.WEBSERVICE_UNAVAILABLE_CODE);
			responseModel.setResponseMessage(ResponseConstants.WEBSERVICE_UNAVAILABLE_MESSAGE);
			
			String response = CommonMethods.ObjectToJsonString(responseModel);
			
			// log request and responses
			CommonMethods.logContent("*** ExternalFundsTransfer Request ***" + body + "\r\n\r\n"
					+ "*** ExternalFundsTransfer Request(Interswitch) *** \r\n\r\n"
					+ "*** ExternalFundsTransfer Response(Interswitch) ***" + fundTransferResponseStr + "\r\n\r\n"
					+ "*** QueryTransaction Response *** \r\n\r\n"
					+ "*** ExternalFundsTransfer Response ***" + response);
						
			return response;
		}
		
		// When responseCodes from FundTransfer object IS NOT one of the success/pending codes
		if (!(QuicktellerConstants.SUCCESS_CODES.toUpperCase().contains(fundTransferResponse.getResponseCode()))) {
			
			//set response object appropriately
			String message = "Interswitch ResponseCode: " + fundTransferResponse.getResponseCode() + "\r\n"
							+ "Interswitch ResponseMessage" + fundTransferResponse.getResponseCodeGrouping();
			
			responseModel.setResponseCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage(message);
			
		}
		
		// When responseCodes from Error object IS NOT one of the success/pending codes
		if (!(QuicktellerConstants.SUCCESS_CODES.toUpperCase().contains(errorResp.error.getCode()))) {

			// set response object appropriately
			String message = "Interswitch ResponseCode: " + errorResp.error.getCode() + "\r\n"
					+ "Interswitch ResponseMessage" + errorResp.error.getMessage();

			responseModel.setResponseCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage(message);

		}
		
		// used when saving transaction to the db
		String transferCode = fundTransferResponse.getTransferCode() + "|" + new Date() + "|" + new Date().getTime();
		
		// call query transaction to confirm transaction
		System.out.println("TransferCode: " + fundTransferResponse.getTransferCode());
		String queryTransRespStr = quickteller.queryTransaction(fundTransferResponse.getTransferCode());
		
		// convert response to QueryTransactionResponse object
		QueryTransactionResponse queryTransactionResponse = (QueryTransactionResponse) CommonMethods.JSONStringToObject(queryTransRespStr, QueryTransactionResponse.class);

		// convert response to Error object
		Errors errorResponse = (Errors) CommonMethods.JSONStringToObject(queryTransRespStr, Errors.class);
		
		
		// if Error object was returned and response code is not one of the success codes
		if (!QuicktellerConstants.SUCCESS_CODES.contains(errorResponse.error.getCode().trim())) {
			
//			// call procedure to do a reversal (credit)
//			System.out.println("\n\n Credit Customer");
//			database.isTransactionSuccessful(senderName, externalTransfer, true, ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, transferCode);
			
			//save reversed transaction
			new DBConnection().saveSupposedReversal(externalTransfer.getFromAccountNumber(), 
					externalTransfer.getBeneficiaryAccountNumber(), 
					CommonMethods.koboToNaira(Integer.parseInt(externalTransfer.getTransactionAmount().trim())),
					errorResponse.error.getCode(),
					errorResponse.error.getMessage(), 
					"ExternalFundTransfer", 
					CommonMethods.koboToNaira(Double.parseDouble(externalTransfer.getChargeAmount().trim())),
					CommonMethods.koboToNaira(Double.parseDouble(externalTransfer.getTaxAmount().trim())));
			
			
			//set response object appropriately
			String message = "Interswitch ResponseCode: " + errorResponse.error.getCode() + "\r\n"
							+ "Interswitch ResponseMessage" + errorResponse.error.getMessage();
			
			responseModel.setResponseCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage(responseModel.getResponseMessage().isEmpty()
										? message
										: responseModel.getResponseMessage());
			
			String response = CommonMethods.ObjectToJsonString(responseModel);
			
			// log request and responses
			CommonMethods.logContent("*** ExternalFundsTransfer Request ***" + body + "\r\n\r\n"
					+ "*** ExternalFundsTransfer Request(Interswitch) *** \r\n\r\n"
					+ "*** ExternalFundsTransfer Response(Interswitch) ***" + fundTransferResponseStr + "\r\n\r\n"
					+ "*** QueryTransaction Response ***" + queryTransRespStr + "\r\n\r\n"
					+ "*** ExternalFundsTransfer Response ***" + response);
			
			return response;
		}
				
		// successful, proceed
		
		// get transactionResponseCode
		String transactionResponseCode = queryTransactionResponse.getTransactionResponseCode().trim();
		
		// if response code returned is not one of the success codes
		if (!(QuicktellerConstants.SUCCESS_CODES.toUpperCase().contains(transactionResponseCode))) {

//			// call procedure to do a reversal (credit)
//			System.out.println("\n\n Credit Customer");
//			database.isTransactionSuccessful(senderName, externalTransfer, true, ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, transferCode);
			
			//save reversed transaction
			new DBConnection().saveSupposedReversal(externalTransfer.getFromAccountNumber(), 
					externalTransfer.getBeneficiaryAccountNumber(), 
					CommonMethods.koboToNaira(Integer.parseInt(externalTransfer.getTransactionAmount().trim())),
					transactionResponseCode,
					queryTransactionResponse.getStatus(), 
					"ExternalFundTransfer", 
					CommonMethods.koboToNaira(Double.parseDouble(externalTransfer.getChargeAmount().trim())),
					CommonMethods.koboToNaira(Double.parseDouble(externalTransfer.getTaxAmount().trim())));
			
			
			//set response object appropriately
			String message = "Interswitch ResponseCode: " + transactionResponseCode + "\r\n"
					+ "Interswitch ResponseMessage" + queryTransactionResponse.getStatus();
			
			responseModel.setResponseCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage(responseModel.getResponseMessage().isEmpty()
										? message
										: responseModel.getResponseMessage());
			
		} else {
			//set response object appropriately
			responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}
		
		String response = CommonMethods.ObjectToJsonString(responseModel);
		
		// log request and responses
		CommonMethods.logContent("*** ExternalFundsTransfer Request ***" + body + "\r\n\r\n"
				+ "*** ExternalFundsTransfer Request(Interswitch) ***" + "fundTransferReqStr" + "\r\n\r\n"
				+ "*** ExternalFundsTransfer Response(Interswitch) ***" + fundTransferResponseStr + "\r\n\r\n"
				+ "*** QueryTransaction Response ***" + queryTransRespStr + "\r\n\r\n"
				+ "*** ExternalFundsTransfer Response ***" + response);

		System.out.println("\n**** End ExternalTransfers(finish) ****");
				
		return response;
		
	}
	

	
	
	
	public static void main(String[] args) {
		String req = "{\r\n" + 
				"	\"initiatingApp\": \"MAPP\",\r\n" + 
				"	\"fromAccountNumber\": \"2002132691\",\r\n" + 
				"	\"toAccountNumber\": \"2002083563\",\r\n" + 
				"	\"transactionAmount\": \"500000\",\r\n" + 
				"	\"transactionDescription\": \"test today\",	\r\n" + 
				"	\"chargeAmount\": \"0\",\r\n" + 
				"	\"taxAmount\": \"0\"\r\n" + 
				"}";
		
		System.out.println(new FundTransferService().InternalFundTransfer(req));
	}
	
	
}
