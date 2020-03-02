package com.company.application.cardManagement;

import java.util.Date;

import com.company.application.account.AccountService;
import com.company.application.cardManagement.data.CardHotlistRequest;
import com.company.application.cardManagement.data.CardOwnedResponse;
import com.company.application.cardManagement.data.CardReplacementRequest;
import com.company.application.cardManagement.data.NewCardRequest;
import com.company.application.newCustomer.NewCustomerService;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;

public class CardManagementService {

	public CardManagementService() {
		// creates necessary tables and procedures if not already created
		new CardManagementDBOperation().executeScript();
	}
	
	public CardOwnedResponse cardOwned(String accountNumber) {
		
		CardManagementDBOperation database = new CardManagementDBOperation();
		
		CardOwnedResponse cardOwnedResponse = database.getCards(accountNumber);
				
		if (cardOwnedResponse == null) {
			return new CardOwnedResponse(null, ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE);
		}
		
		return cardOwnedResponse;
	}
	
	public ResponseModel newCard(String body) {
		
		NewCardRequest newCardRequest = (NewCardRequest) CommonMethods.JSONStringToObject(body, NewCardRequest.class);
		
		// validate parameters
		ResponseModel responseModel = validateParameter(newCardRequest);
		
		// return if validation fails
		if (!responseModel.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE))
			return responseModel;
		
		// proceed, with request and save application
		responseModel = requestCard(newCardRequest.getAccountNumber(), newCardRequest.getCardType(), newCardRequest.getNameOnCard());
		
		return responseModel;
	}
	
	public ResponseModel cardHotlist(String body) {
		
		CardHotlistRequest cardHotlistRequest = (CardHotlistRequest) CommonMethods.JSONStringToObject(body, CardHotlistRequest.class);
		
		// validate parameters
		ResponseModel responseModel = validateParameter(cardHotlistRequest);
		
		// return if validation fails
		if (!responseModel.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE))
			return responseModel;
		
		// validate that card exist
		if(!cardExist(cardHotlistRequest.getAccountNumber(), cardHotlistRequest.getCardPan())) {
			return new ResponseModel(ResponseConstants.NOT_FOUND_CODE, ResponseConstants.NOT_FOUND_MESSAGE);
		}
		
		// proceed, with request and save application
		responseModel = hotlistCard(cardHotlistRequest.getAccountNumber(), cardHotlistRequest.getCardPan());
		
		return responseModel;
	}
	
	public ResponseModel cardReplacement(String body) {
		
		CardReplacementRequest cardReplacementRequest = (CardReplacementRequest) CommonMethods.JSONStringToObject(body, CardReplacementRequest.class);
		
		// validate parameters
		ResponseModel responseModel = validateParameter(cardReplacementRequest);

		// return if validation fails
		if (!responseModel.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE))
			return responseModel;

		// validate that card exist
		if (!cardExist(cardReplacementRequest.getAccountNumber(), cardReplacementRequest.getCardPan())) {
			return new ResponseModel(ResponseConstants.NOT_FOUND_CODE, ResponseConstants.NOT_FOUND_MESSAGE);
		}

		// proceed, with request and save application
		responseModel = replaceCard(cardReplacementRequest.getAccountNumber(), cardReplacementRequest.getCardPan(),
								cardReplacementRequest.getDeliveryType(), cardReplacementRequest.getDeliveryAddress());

		return responseModel;
	}
	
	
	
	private ResponseModel validateParameter(NewCardRequest newCardRequest) {
		
		ResponseModel responseModel = new ResponseModel();
		responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		
		if (newCardRequest.getAccountNumber().isEmpty()) {
			
			responseModel.setResponseCode(ResponseConstants.MANDATORY_CODE);
			responseModel.setResponseMessage(ResponseConstants.MANDATORY_MESSAGE + "AccountNumber");
		}
		if (newCardRequest.getCardType().isEmpty()) {
			
			responseModel.setResponseCode(ResponseConstants.MANDATORY_CODE);
			responseModel.setResponseMessage(ResponseConstants.MANDATORY_MESSAGE + "CardType");
		}
		if (newCardRequest.getNameOnCard().isEmpty()) {
			
			responseModel.setResponseCode(ResponseConstants.MANDATORY_CODE);
			responseModel.setResponseMessage(ResponseConstants.MANDATORY_MESSAGE + "NameOnCard");
		}
		
		String accountName = new AccountService().accountName(newCardRequest.getAccountNumber());
		if (accountName.isEmpty()) {
			responseModel.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
			responseModel.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE + " - AccountNumber(" + newCardRequest.getAccountNumber() + ")");			
		}
		
		return responseModel;
	}
	
	private ResponseModel validateParameter(CardHotlistRequest cardHotlistRequest) {
		
		ResponseModel responseModel = new ResponseModel();
		responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		
		if (cardHotlistRequest.getAccountNumber().isEmpty()) {
			
			responseModel.setResponseCode(ResponseConstants.MANDATORY_CODE);
			responseModel.setResponseMessage(ResponseConstants.MANDATORY_MESSAGE + "AccountNumber");
		}
		if (cardHotlistRequest.getCardPan().isEmpty()) {
			
			responseModel.setResponseCode(ResponseConstants.MANDATORY_CODE);
			responseModel.setResponseMessage(ResponseConstants.MANDATORY_MESSAGE + "CardPan");
		}
		
		return responseModel;
	}
	
	private ResponseModel validateParameter(CardReplacementRequest cardReplacementRequest) {
		
		ResponseModel responseModel = new ResponseModel();
		responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		
		if (cardReplacementRequest.getAccountNumber().isEmpty()) {
			
			responseModel.setResponseCode(ResponseConstants.MANDATORY_CODE);
			responseModel.setResponseMessage(ResponseConstants.MANDATORY_MESSAGE + "AccountNumber");
		}
		if (cardReplacementRequest.getCardPan().isEmpty()) {
			
			responseModel.setResponseCode(ResponseConstants.MANDATORY_CODE);
			responseModel.setResponseMessage(ResponseConstants.MANDATORY_MESSAGE + "CardPan");
		}
		if (cardReplacementRequest.getDeliveryType().isEmpty()) {
			
			responseModel.setResponseCode(ResponseConstants.MANDATORY_CODE);
			responseModel.setResponseMessage(ResponseConstants.MANDATORY_MESSAGE + "DeliveryType");
		}
		if (cardReplacementRequest.getDeliveryAddress().isEmpty()) {
			
			responseModel.setResponseCode(ResponseConstants.MANDATORY_CODE);
			responseModel.setResponseMessage(ResponseConstants.MANDATORY_MESSAGE + "DeliveryAddress");
		}
		
		return responseModel;
	}
	
	private ResponseModel requestCard(String accountNumber, String cardtype, String nameOnCard){
		
		String customerNumber = new NewCustomerService().customerInfo(accountNumber).getCustomerNumber();
		
		CardManagementDBOperation database = new CardManagementDBOperation();
		
		// execute procedure
		ResponseModel responseModel = database.procedureNSP_CARD_REQUEST(
											"Create",		//PV_MAIN_OPERATION
											customerNumber,	//PV_CUST_NO
											accountNumber,	//PV_ACCT_NO
											cardtype,		//PV_CARD_TYPE
											nameOnCard,		//PV_NAME_ON_CARD
											"A", 			//PV_REC_ST
											1,				//PV_VERSION_NO
											new Date()+"",	//PV_ROW_TS
											"NEPTUNE", 		//PV_USER_ID
											new Date()+"",	//PV_CREATE_DT
											new Date()+"",	//PV_SYS_CREATE_TS
											"SYSTEM", 		//PV_CREATED_BY
											"123456789", 	//PV_REQUEST_PWD
											"DB",			//PV_LOG_REPOSITORY
											0,				//PV_TEST_CALL_LEVEL
											3,				//PV_TRACE_LEVEL
											"",				//PV_ERROR_CODE
											0,				//PV_ERROR_SEVERITY
											"");			//PV_ERROR_MESSAGE

		// save request
		String narration = "Mobile App Channel: New CARD REQUEST by - " +  accountNumber + " - " + nameOnCard;
		database.saveRequest("CARDHOTLIST", customerNumber, accountNumber, "", narration, "CARD REQUEST", responseModel.getResponseCode());
		
		System.out.println("procedure NSP_CARD_REQUEST Response: \n" + CommonMethods.ObjectToJsonString(responseModel));
			
		return responseModel;
	}
	
	private ResponseModel hotlistCard(String accountNumber, String cardPan){
		
		String customerNumber = new NewCustomerService().customerInfo(accountNumber).getCustomerNumber();
		
		CardManagementDBOperation database = new CardManagementDBOperation();
		
		// execute procedure
		ResponseModel responseModel = database.procedureNSP_CARD_HOTLIST(
											"Create",		//PV_MAIN_OPERATION
											customerNumber, //PV_CUST_NO
											accountNumber,	//PV_ACCT_NO
											cardPan,		//PV_CARD_TYPE
											"A", 			//PV_REC_ST
											1,				//PV_VERSION_NO
											new Date()+"",	//PV_ROW_TS
											"NEPTUNE", 		//PV_USER_ID
											new Date()+"",	//PV_CREATE_DT
											new Date()+"",	//PV_SYS_CREATE_TS
											"SYSTEM", 		//PV_CREATED_BY
											"123456789", 	//PV_REQUEST_PWD
											"DB",			//PV_LOG_REPOSITORY
											0,				//PV_TEST_CALL_LEVEL
											3,				//PV_TRACE_LEVEL
											"",				//PV_ERROR_CODE
											0,				//PV_ERROR_SEVERITY
											"");			//PV_ERROR_MESSAGE
		
		// save request
		String narration = "Mobile App Channel: CARD HOTLIST by - " +  accountNumber + " - " + cardPan;
		database.saveRequest("CARDHOTLIST", customerNumber, accountNumber, cardPan, narration, "CARD BLOCK", responseModel.getResponseCode());
		
		System.out.println("procedure NSP_CARD_HOTLIST Response: \n" + CommonMethods.ObjectToJsonString(responseModel));
			
		return responseModel;
		
	}
		
	private ResponseModel replaceCard(String accountNumber, String cardPan, String deliveryType, String deliveryAddress) {
		
		String customerNumber = new NewCustomerService().customerInfo(accountNumber).getCustomerNumber();
		
		CardManagementDBOperation database = new CardManagementDBOperation();
		
		// execute procedure
		ResponseModel responseModel = database.procedureNSP_CARD_REPLACEMENT(
											"Create",			//PV_MAIN_OPERATION
											accountNumber, 		//PV_CUST_NO
											customerNumber,		//PV_ACCT_NO
											cardPan,			//PV_CARD_TYPE
											deliveryType, 		//PV_DELIVERY_TYPE
											deliveryAddress,	// PV_DELIVERY_ADDRESS
											"A", 				//PV_REC_ST
											1,					//PV_VERSION_NO
											new Date()+"",		//PV_ROW_TS
											"NEPTUNE", 			//PV_USER_ID
											new Date()+"",		//PV_CREATE_DT
											new Date()+"",		//PV_SYS_CREATE_TS
											"SYSTEM", 			//PV_CREATED_BY
											"123456789", 		//PV_REQUEST_PWD
											"DB",				//PV_LOG_REPOSITORY
											0,					//PV_TEST_CALL_LEVEL
											3,					//PV_TRACE_LEVEL
											"",					//PV_ERROR_CODE
											0,					//PV_ERROR_SEVERITY
											"");				//PV_ERROR_MESSAGE

		

		// save request
		String narration = "Mobile App Channel: CARD REPLACEMENT request by - " +  accountNumber + " - " + cardPan;
		database.saveRequest("CARDHOTLIST", customerNumber, accountNumber, cardPan, narration, "CARD REPLACEMENT", responseModel.getResponseCode());
		
		System.out.println("procedure NSP_CARD_REPLACEMENT Response: \n" + CommonMethods.ObjectToJsonString(responseModel));
			
		return responseModel;
		
	}
		
	private boolean cardExist(String accountNumber, String cardPan) {
		CardManagementDBOperation database = new CardManagementDBOperation();
		
		CardOwnedResponse cardOwnedResponse = database.getCards(accountNumber, cardPan);
				
		if (cardOwnedResponse == null) {
			return false;
		}
		
		if (!cardOwnedResponse.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE)) {
			return false;
		}
		
		return true;
		
	}


	
	public static void main(String[] args) {
		
		CardManagementService cardServices = new CardManagementService();
		
		System.out.println("card owned: " + System.currentTimeMillis() + " \n" + CommonMethods.ObjectToJsonString(cardServices.cardOwned("3002015994")));
		
		//NewCardRequest newCardRequest = new NewCardRequest("3002015994", "Master Card", "test");
		//System.out.println("card request: \n" + CommonMethods.ObjectToJsonString(cardServices.newCard(newCardRequest)));
		
		//CardHotlistRequest cardHotlistRequest = new CardHotlistRequest("3002015994", "5061240201527008503");
		//System.out.println("card hotlist: \n" + CommonMethods.ObjectToJsonString(cardServices.cardHotlist(cardHotlistRequest)));
		
		//CardReplacementRequest cardReplacementRequest = new CardReplacementRequest("3002015994", "5061240201527008503", "YABA LAGOS", "MAIL");
		//System.out.println("card replacement: \n" + CommonMethods.ObjectToJsonString(cardServices.cardReplacement(CommonMethods.ObjectToJsonString(cardReplacementRequest))));		
		
	}
	
}
