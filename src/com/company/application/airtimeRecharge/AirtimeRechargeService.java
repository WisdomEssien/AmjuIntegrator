package com.company.application.airtimeRecharge;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import com.company.application.airtimeRecharge.data.AirtimeRechargeRequest;
import com.company.application.balanceEnquiry.BalanceEnquiryService;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.QuicktellerConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.QueryTransaction.QueryTransactionResponse;
import com.neptunesoftware.reuseableClasses.Quickteller.SendBillsPaymentAdvice.BillPaymentAdviceResponse;
import com.neptunesoftware.reuseableClasses.Quickteller.data.Errors;


public class AirtimeRechargeService {

	/*
	 * This Method works with the assumption that transaction amount received is in kobo
	 */
	
	public String AirtimeRecharge(String body) {
		System.out.println("\n**** Start AirtimeRecharge ****");
		
		AirtimeRechargeDBOperation database = new AirtimeRechargeDBOperation();
		
		
		AirtimeRechargeRequest airtimeRchgeXfer = (AirtimeRechargeRequest) CommonMethods.JSONStringToObject(body, AirtimeRechargeRequest.class);
		
		// convert transaction amount to Naira
		double tran_Amount = CommonMethods.koboToNaira(Integer.parseInt(airtimeRchgeXfer.getTransactionAmount().trim()));
		double charge_Amount = CommonMethods.koboToNaira(Double.parseDouble(airtimeRchgeXfer.getChargeAmount().trim()));
		double tax_Amount = CommonMethods.koboToNaira(Double.parseDouble(airtimeRchgeXfer.getTaxAmount().trim()));

		// total charge
		double charges = Double.sum(charge_Amount, tax_Amount);
		// total amount
		double totalAmount = Double.sum(tran_Amount, charges);
		
		System.out.println("Total amount to be deducted: " + totalAmount);
		// check to make sure account has sufficient balance
		if (!new BalanceEnquiryService().hasSufficientFunds(airtimeRchgeXfer.getFromAccountNumber(), totalAmount)) {

			// return insufficient funds when transaction amount is less than available
			// balance
			ResponseModel responseModel = new ResponseModel();
			responseModel.setResponseCode(ResponseConstants.INSUFFICIENT_CODE);
			responseModel.setResponseMessage(ResponseConstants.INSUFFICIENT_MESSAGE);
			
			String response = CommonMethods.ObjectToJsonString(responseModel);
			
			// log request and responses
			CommonMethods.logContent("*** AirtimeRecharge Request ***" + body + "\r\n\r\n"
					+ "*** AirtimeRecharge Request(Interswitch) *** \r\n\r\n"
					+ "*** AirtimeRecharge Response(Interswitch) *** \r\n\r\n"
					+ "*** QueryTransaction Response *** \r\n\r\n"
					+ "*** AirtimeRecharge Response ***" + response);
						
			return response;
		}
		
		// call procedure to deduct from the senders account
		System.out.println("\n\n Debit Customer");
		if (!database.isTransactionSuccessful(airtimeRchgeXfer, false, "", "")) {

			// return error if deduction was not possible
			ResponseModel responseModel = new ResponseModel();
			responseModel.setResponseCode(ResponseConstants.PROCEDURE_CODE);
			responseModel.setResponseMessage(ResponseConstants.PROCEDURE_MESSAGE);
			
			String response = CommonMethods.ObjectToJsonString(responseModel);
			
			// log request and responses
			CommonMethods.logContent("*** AirtimeRecharge Request ***" + body + "\r\n\r\n"
					+ "*** AirtimeRecharge Request(Interswitch) *** \r\n\r\n"
					+ "*** AirtimeRecharge Response(Interswitch) *** \r\n\r\n"
					+ "*** QueryTransaction Response *** \r\n\r\n"
					+ "*** AirtimeRecharge Response ***" + response);
						
			return response;
		}
			
		// deduction was successful proceed

		// call Interswitch to carryout transfer
		Quickteller quickteller = new Quickteller();
		
		AtomicReference<String> requestReference = new AtomicReference<String>();
		
		String BPAdviceRespStr = quickteller.sendBillPaymentAdvice(airtimeRchgeXfer.getPaymentCode(),
				airtimeRchgeXfer.getCustomerId(), airtimeRchgeXfer.getMobileNumber(), "", airtimeRchgeXfer.getTransactionAmount(),
				requestReference);

		// Deserialize response string
		BillPaymentAdviceResponse BPAdviceResp = (BillPaymentAdviceResponse) CommonMethods.JSONStringToObject(BPAdviceRespStr, BillPaymentAdviceResponse.class);
		
		//Also deserialize response string to Error object incase error object was returned not BillPaymentAdviceResponse
		Errors errorResp = (Errors) CommonMethods.JSONStringToObject(BPAdviceRespStr, Errors.class);
		
		ResponseModel responseModel = new ResponseModel();
		
		// Return when neither BillPaymentAdviceResponse nor Error object was returned
		if (BPAdviceRespStr == null || BPAdviceRespStr.isEmpty()) {
			
			// call procedure to do a reversal (credit)
			System.out.println("\n\n Credit Customer");
			database.isTransactionSuccessful(airtimeRchgeXfer, true, ResponseConstants.WEBSERVICE_UNAVAILABLE_CODE, "");

			// and return if it is an error object
			responseModel.setResponseCode(ResponseConstants.WEBSERVICE_UNAVAILABLE_CODE);
			responseModel.setResponseMessage(ResponseConstants.WEBSERVICE_UNAVAILABLE_MESSAGE);
			
			String response = CommonMethods.ObjectToJsonString(responseModel);
			
			// log request and responses
			CommonMethods.logContent("*** AirtimeRecharge Request ***" + body + "\r\n\r\n"
					+ "*** AirtimeRecharge Request(Interswitch) *** \r\n\r\n"
					+ "*** AirtimeRecharge Response(Interswitch) ***" + BPAdviceRespStr + "\r\n\r\n"
					+ "*** QueryTransaction Response *** \r\n\r\n"
					+ "*** AirtimeRecharge Response ***" + response);
						
			return response;
		}

		// When responseCodes from BillPaymentAdviceResponse object IS NOT one of the success/pending codes
		if (!(QuicktellerConstants.SUCCESS_CODES.toUpperCase().contains(BPAdviceResp.getResponseCode()))) {
			
			//set response object appropriately
			String message = "Interswitch ResponseCode: " + BPAdviceResp.getResponseCode() + "\r\n"
							+ "Interswitch ResponseMessage" + BPAdviceResp.getResponseMessage();
			
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
				
		
		// used when saving to the db
		String transferCode = requestReference.get() + "|" + new Date() + "|" + new Date().getTime();

		// call query transaction to confirm transaction
		System.out.println("ReferenceCode: " + requestReference.get());
		String queryTransRespStr = quickteller.queryTransaction(requestReference.get());
		
		// convert response to QueryTransactionResponse object
		QueryTransactionResponse queryTransactionResponse = (QueryTransactionResponse) CommonMethods.JSONStringToObject(queryTransRespStr, QueryTransactionResponse.class);

		// convert response to Error object
		Errors errorResponse = (Errors) CommonMethods.JSONStringToObject(queryTransRespStr, Errors.class);
				
		// if Error object was returned and response code is not one of the success codes
		if (!QuicktellerConstants.SUCCESS_CODES.contains(errorResponse.error.getCode().trim())) {
			
			// call procedure to do a reversal (credit)
			System.out.println("\n\n Credit Customer");
			database.isTransactionSuccessful(airtimeRchgeXfer, true, ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, transferCode);
			
			//set response object appropriately
			String message = "Interswitch ResponseCode: " + errorResponse.error.getCode() + "\r\n"
							+ "Interswitch ResponseMessage" + errorResponse.error.getMessage();
			
			responseModel.setResponseCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
			responseModel.setResponseMessage(responseModel.getResponseMessage().isEmpty()
										? message
										: responseModel.getResponseMessage());
			
			String response = CommonMethods.ObjectToJsonString(responseModel);
			
			// log request and responses
			CommonMethods.logContent("*** AirtimeRecharge Request ***" + body + "\r\n\r\n"
					+ "*** AirtimeRecharge Request(Interswitch) *** \r\n\r\n"
					+ "*** AirtimeRecharge Response(Interswitch) ***" + BPAdviceRespStr + "\r\n\r\n"
					+ "*** QueryTransaction Response ***" + queryTransRespStr + "\r\n\r\n"
					+ "*** AirtimeRecharge Response ***" + response);
			
			return response;
		}

		// successful, proceed
		// get transactionResponseCode
		String transactionResponseCode = queryTransactionResponse.getTransactionResponseCode();
		
		// if response code returned is not one of the success codes
		if (!(QuicktellerConstants.SUCCESS_CODES.toUpperCase().contains(transactionResponseCode))) {
			
			// call procedure to do a reversal (credit)
			System.out.println("\n\n Credit Customer");
			database.isTransactionSuccessful(airtimeRchgeXfer, true, ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, transferCode);

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
		CommonMethods.logContent("*** AirtimeRecharge Request ***" + body + "\r\n\r\n"
				+ "*** AirtimeRecharge Request(Interswitch) *** \r\n\r\n"
				+ "*** AirtimeRecharge Response(Interswitch) ***" + BPAdviceRespStr + "\r\n\r\n"
				+ "*** QueryTransaction Response ***" + queryTransRespStr + "\r\n\r\n"
				+ "*** AirtimeRecharge Response ***" + response);

		System.out.println("\n**** End AirtimeRecharge(finish) ****");
		
		return CommonMethods.ObjectToJsonString(responseModel);
	}

	
}
