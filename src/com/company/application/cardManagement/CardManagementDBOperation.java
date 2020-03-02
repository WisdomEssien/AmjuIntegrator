package com.company.application.cardManagement;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.company.application.cardManagement.data.CardOwnedResponse;
import com.company.application.cardManagement.data.CardProperty;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Database.DBConnection;
import com.neptunesoftware.reuseableClasses.Database.ValueDatatypePair;

public class CardManagementDBOperation extends DBConnection{

	public CardManagementDBOperation() {
		super();
	}
	
	public CardManagementDBOperation(final String databaseName) {
		super(databaseName);
	}
		
	public CardManagementDBOperation(final String driver, final String connectionURL, final String username, final String password, final String databaseType) {
		super(driver, connectionURL, username, password, databaseType);
	}
	
	
	protected void executeScript() {
		
		String query = "";
		
		if (!tableExist("ALT_QTELLER_MB_CARD")) {
			
			query = "CREATE TABLE ALT_QTELLER_MB_CARD\r\n" + 
					"(\r\n" + 
					"  TRAN_REF      VARCHAR2(60 BYTE)               NOT NULL,\r\n" + 
					"  CUST_NO       VARCHAR2(50 BYTE),\r\n" + 
					"  ACCT_NO       VARCHAR2(50 BYTE)               NOT NULL,\r\n" + 
					"  CARD_PAN      VARCHAR2(50 BYTE),\r\n" + 
					"  NARRATION     VARCHAR2(900 BYTE),\r\n" + 
					"  SUB_TRAN_REF  VARCHAR2(300 BYTE),\r\n" + 
					"  RESPONSE_CD   VARCHAR2(10 BYTE),\r\n" + 
					"  REC_TS        TIMESTAMP(6)                    DEFAULT Current_timestamp\r\n" + 
					")";
			
			isDatabaseObjectCreated(query);
		}
		
	}
	
	public CardOwnedResponse getCards(final String accountNumber) {
								
		CardOwnedResponse cardOwnedResponse = new CardOwnedResponse();
		cardOwnedResponse.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
		cardOwnedResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
		
			String query = " SELECT PAN cardpan, to_char(EXPIRY_DATE, 'DD-MON-yyyy') expirydate,ACCOUNT_NUMBER cardtype, NAME_ON_CARD nameOnCard\r\n" + 
					" FROM ATM_USER WHERE ACCOUNT_NUMBER = ? ";

			// input parameters in the order needed in the query
			List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
			params.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));

			// collect the result
			HashMap<Integer, HashMap<String, String>> records = executeSelect(query, params);

			// Null is returned when an exception is thrown.
			if (records == null) {
				return null;
			}

			List<CardProperty> cards = new ArrayList<CardProperty>();

			// Loop through each row returned
			for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
				// collect the columns and access its value by
				// first the column alias or column names as returned by the query

				CardProperty cardProperty = new CardProperty();
				cardProperty.setCardPan(records.get(rowIndex).get("cardpan".toUpperCase()));
				cardProperty.setExpiryDate(records.get(rowIndex).get("expirydate".toUpperCase()));
				cardProperty.setCardType(records.get(rowIndex).get("cardtype".toUpperCase()));
				cardProperty.setNameOnCard(records.get(rowIndex).get("nameOnCard".toUpperCase()));
				
				cardProperty.setCardBlocked(isCardBlocked(accountNumber, records.get(rowIndex).get("cardpan".toUpperCase())));
				
				cards.add(cardProperty);
				
				cardOwnedResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
				cardOwnedResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
			}			
			
			cardOwnedResponse.setCards(cards);
			cardOwnedResponse.setCanRequestForCard(daysAfterCardRequest(accountNumber) > 4 ? true : false);
			
			return cardOwnedResponse;		
	}
	
	public CardOwnedResponse getCards(final String accountNumber, final String cardPan) {

		CardOwnedResponse cardOwnedResponse = new CardOwnedResponse();
		cardOwnedResponse.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
		cardOwnedResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);

		String query = " SELECT PAN cardpan, EXPIRY_DATE expirydate,ACCOUNT_NUMBER cardtype, NAME_ON_CARD nameOnCard \r\n"
				+ " FROM ATM_USER WHERE ACCOUNT_NUMBER = ?  AND PAN = ? ";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));
		params.add(new ValueDatatypePair(cardPan, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(query, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		List<CardProperty> cards = new ArrayList<CardProperty>();

		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			CardProperty cardProperty = new CardProperty();
			cardProperty.setCardPan(records.get(rowIndex).get("cardpan".toUpperCase()));
			cardProperty.setExpiryDate(records.get(rowIndex).get("expirydate".toUpperCase()));
			cardProperty.setCardType(records.get(rowIndex).get("cardtype".toUpperCase()));
			cardProperty.setNameOnCard(records.get(rowIndex).get("nameOnCard".toUpperCase()));

			cardProperty.setCardBlocked(isCardBlocked(accountNumber, records.get(rowIndex).get("cardpan".toUpperCase())));
			
			cards.add(cardProperty);

			cardOwnedResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			cardOwnedResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}

		cardOwnedResponse.setCards(cards);
		cardOwnedResponse.setCanRequestForCard(daysAfterCardRequest(accountNumber) > 4 ? true : false);

		return cardOwnedResponse;
		
	}
	
	
	private int daysAfterCardRequest(final String accountNumber) {
		
		String query = "SELECT ROUND(SYSDATE - TO_DATE(TO_CHAR(NVL(MAX(REC_TS), SYSDATE - 10), 'DD/MM/YYYY'), 'DD/MM/YYYY')) DaysAfterCardRequest \r\n" + 
				" FROM ALT_QTELLER_MB_CARD \r\n" + 
				" WHERE ACCT_NO = ? \r\n" + 
				" AND UPPER(SUB_TRAN_REF) = 'CARD REQUEST' \r\n" + 
				" AND RESPONSE_CD = '00'";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(query, params);

		int noOfDaysAfterCardRequest = 0;
		
		// Null is returned when an exception is thrown.
		if (records == null) {
			return noOfDaysAfterCardRequest;
		}
		
		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			noOfDaysAfterCardRequest = Integer.valueOf(records.get(rowIndex).get("DaysAfterCardRequest".toUpperCase()));
		}
		
		return noOfDaysAfterCardRequest;
	}
	
	private boolean isCardBlocked(final String accountNumber, final String cardPan) {
		
		String query = "SELECT A.*, ROUND(SYSDATE - TO_DATE(TO_CHAR(REC_TS, 'DD/MM/YYYY'), 'DD/MM/YYYY')) DaysAfterCardBlock\r\n" + 
				" FROM ALT_QTELLER_MB_CARD A\r\n" + 
				" WHERE ACCT_NO = ? \r\n" + 
				" AND CARD_PAN = ? \r\n" + 
				" AND UPPER(SUB_TRAN_REF) = 'CARD BLOCK' \r\n" + 
				" AND RESPONSE_CD = '00'";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));
		params.add(new ValueDatatypePair(cardPan, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(query, params);

		boolean isCardBlocked = false;
		
		// Null is returned when an exception is thrown.
		if (records == null) {
			return isCardBlocked;
		}
		
		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			isCardBlocked = true;
		}
		
		return isCardBlocked;
	}
	
	
	
	public ResponseModel procedureNSP_CARD_REQUEST(String PV_MAIN_OPERATION, String  PV_CUST_NO, String  PV_ACCT_NO,       
			String PV_CARD_TYPE, String PV_NAME_ON_CARD, String PV_REC_ST , int PV_VERSION_NO,  String PV_ROW_TS,             
			  String PV_USER_ID,  String PV_CREATE_DT, String PV_SYS_CREATE_TS, String PV_CREATED_BY,
			  String PV_REQUEST_PWD, String PV_LOG_REPOSITORY, int PV_TEST_CALL_LEVEL, int PV_TRACE_LEVEL ,   
			  String  PV_ERROR_CODE,  int PV_ERROR_SEVERITY, String PV_ERROR_MESSAGE ){

		// create a list of IN parameters with their corresponding indices in the order specified by the procedure
		LinkedHashMap<Integer, ValueDatatypePair> inParam = new LinkedHashMap<Integer, ValueDatatypePair>();
		inParam.put(1, new ValueDatatypePair(PV_MAIN_OPERATION, Types.VARCHAR));
		inParam.put(2, new ValueDatatypePair(PV_CUST_NO, Types.VARCHAR));
		inParam.put(3, new ValueDatatypePair(PV_ACCT_NO, Types.VARCHAR));
		inParam.put(4, new ValueDatatypePair(PV_CARD_TYPE, Types.VARCHAR));
		inParam.put(5, new ValueDatatypePair(PV_NAME_ON_CARD, Types.VARCHAR));
		inParam.put(6, new ValueDatatypePair(PV_REC_ST, Types.VARCHAR));
		inParam.put(7, new ValueDatatypePair(PV_VERSION_NO, Types.INTEGER));
		inParam.put(8, new ValueDatatypePair(PV_ROW_TS, Types.DATE));
		inParam.put(9, new ValueDatatypePair(PV_USER_ID, Types.VARCHAR));
		inParam.put(10, new ValueDatatypePair(PV_CREATE_DT, Types.DATE));
		inParam.put(11, new ValueDatatypePair(PV_SYS_CREATE_TS, Types.DATE));
		inParam.put(12, new ValueDatatypePair(PV_CREATED_BY, Types.VARCHAR));
		inParam.put(13, new ValueDatatypePair(PV_REQUEST_PWD, Types.VARCHAR));
		inParam.put(14, new ValueDatatypePair(PV_LOG_REPOSITORY, Types.VARCHAR));
		inParam.put(15, new ValueDatatypePair(PV_TEST_CALL_LEVEL, Types.INTEGER));
		inParam.put(16, new ValueDatatypePair(PV_TRACE_LEVEL, Types.INTEGER));
		inParam.put(17, new ValueDatatypePair(PV_ERROR_CODE, Types.VARCHAR));
		inParam.put(18, new ValueDatatypePair(PV_ERROR_SEVERITY, Types.INTEGER));
		inParam.put(19, new ValueDatatypePair(PV_ERROR_MESSAGE, Types.VARCHAR));

		// create a list of OUT parameters with their corresponding indices in the order specified by the procedure
		LinkedHashMap<Integer, ValueDatatypePair> outParam = new LinkedHashMap<Integer, ValueDatatypePair>();
		outParam.put(14, new ValueDatatypePair(Types.VARCHAR));
		outParam.put(15, new ValueDatatypePair(Types.INTEGER));
		outParam.put(16, new ValueDatatypePair(Types.INTEGER));
		outParam.put(17, new ValueDatatypePair(Types.VARCHAR));
		outParam.put(18, new ValueDatatypePair(Types.INTEGER));
		outParam.put(19, new ValueDatatypePair(Types.VARCHAR));

		// execute the procedure with it's parameters
		LinkedHashMap<Integer, ValueDatatypePair> resultParam = executeProcedure("NSP_CARD_REQUEST", inParam, outParam);

		// procedure executed successfully if error severity is null or empty
		// that's the reason for initializing error severity
		String errorSeverity = "-1", errorMessage = "";
		if (resultParam.get(0).getValue().equals(ResponseConstants.SUCCEESS_CODE)) {
			errorSeverity = resultParam.get(18).getValue().equals(null) ? "" : resultParam.get(18).getValue();
			errorMessage = resultParam.get(19).getValue();
		}
		
		ResponseModel responseModel = new ResponseModel();
		responseModel.setResponseCode(ResponseConstants.PROCEDURE_CODE);
		responseModel.setResponseMessage(ResponseConstants.PROCEDURE_MESSAGE + errorMessage);

		if (errorSeverity.isEmpty() || Integer.valueOf(errorSeverity) == 0) {
			responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}
		
		return responseModel;
	}
	
	public ResponseModel procedureNSP_CARD_HOTLIST(String PV_MAIN_OPERATION, String PV_CUST_NO ,String PV_ACCT_NO ,       
			String PV_CARD_PAN, String PV_REC_ST ,int PV_VERSION_NO ,  String PV_ROW_TS ,             
			  String PV_USER_ID,String PV_CREATE_DT , String PV_SYS_CREATE_TS ,String PV_CREATED_BY, 
			  String PV_REQUEST_PWD, String PV_LOG_REPOSITORY,int PV_TEST_CALL_LEVEL, int PV_TRACE_LEVEL ,   
			  String  PV_ERROR_CODE, int PV_ERROR_SEVERITY, String PV_ERROR_MESSAGE) {
		
		// create a list of IN parameters with their corresponding indices in the order specified by the procedure
		LinkedHashMap<Integer, ValueDatatypePair> inParam = new LinkedHashMap<Integer, ValueDatatypePair>();
		inParam.put(1, new ValueDatatypePair(PV_MAIN_OPERATION, Types.VARCHAR));
		inParam.put(2, new ValueDatatypePair(PV_CUST_NO, Types.VARCHAR));
		inParam.put(3, new ValueDatatypePair(PV_ACCT_NO, Types.VARCHAR));
		inParam.put(4, new ValueDatatypePair(PV_CARD_PAN, Types.VARCHAR));
		inParam.put(5, new ValueDatatypePair(PV_REC_ST, Types.VARCHAR));
		inParam.put(6, new ValueDatatypePair(PV_VERSION_NO, Types.INTEGER));
		inParam.put(7, new ValueDatatypePair(PV_ROW_TS, Types.DATE));
		inParam.put(8, new ValueDatatypePair(PV_USER_ID, Types.VARCHAR));
		inParam.put(9, new ValueDatatypePair(PV_CREATE_DT, Types.DATE));
		inParam.put(10, new ValueDatatypePair(PV_SYS_CREATE_TS, Types.DATE));
		inParam.put(11, new ValueDatatypePair(PV_CREATED_BY, Types.VARCHAR));
		inParam.put(12, new ValueDatatypePair(PV_REQUEST_PWD, Types.VARCHAR));
		inParam.put(13, new ValueDatatypePair(PV_LOG_REPOSITORY, Types.VARCHAR));
		inParam.put(14, new ValueDatatypePair(PV_TEST_CALL_LEVEL, Types.INTEGER));
		inParam.put(15, new ValueDatatypePair(PV_TRACE_LEVEL, Types.INTEGER));
		inParam.put(16, new ValueDatatypePair(PV_ERROR_CODE, Types.VARCHAR));
		inParam.put(17, new ValueDatatypePair(PV_ERROR_SEVERITY, Types.INTEGER));
		inParam.put(18, new ValueDatatypePair(PV_ERROR_MESSAGE, Types.VARCHAR));

		// create a list of OUT parameters with their corresponding indices in the order specified by the procedure
		LinkedHashMap<Integer, ValueDatatypePair> outParam = new LinkedHashMap<Integer, ValueDatatypePair>();
		outParam.put(13, new ValueDatatypePair(null, Types.VARCHAR));
		outParam.put(14, new ValueDatatypePair(null, Types.INTEGER));
		outParam.put(15, new ValueDatatypePair(null, Types.INTEGER));
		outParam.put(16, new ValueDatatypePair(null, Types.VARCHAR));
		outParam.put(17, new ValueDatatypePair(null, Types.INTEGER));
		outParam.put(18, new ValueDatatypePair(null, Types.VARCHAR));

		// execute the procedure with it's parameters
		LinkedHashMap<Integer, ValueDatatypePair> resultParam = executeProcedure("NSP_CARD_HOTLIST", inParam, outParam);

		// procedure executed successfully if error severity is null or empty
		// that's the reason for initializing error severity
		String errorSeverity = "-1", errorMessage = "";
		if (resultParam.get(0).getValue().equals(ResponseConstants.SUCCEESS_CODE)) {
			errorSeverity = resultParam.get(17).getValue().equals(null) ? "" : resultParam.get(17).getValue();
			errorMessage = resultParam.get(18).getValue();
		}
		
		ResponseModel responseModel = new ResponseModel();
		responseModel.setResponseCode(ResponseConstants.PROCEDURE_CODE);
		responseModel.setResponseMessage(ResponseConstants.PROCEDURE_MESSAGE + errorMessage);

		if (errorSeverity.isEmpty() || Integer.valueOf(errorSeverity) == 0) {
			responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}
		
		return responseModel;
	}
	
	public ResponseModel procedureNSP_CARD_REPLACEMENT(String PV_MAIN_OPERATION, String PV_ACCT_NO, String PV_CUST_NO ,       
			String PV_CARD_PAN, String PV_DELIVERY_TYPE, String PV_DELIVERY_ADDRESS, String PV_REC_ST,
			int PV_VERSION_NO, String PV_ROW_TS, String PV_USER_ID , String PV_CREATE_DT, String PV_SYS_CREATE_TS, String PV_CREATED_BY, 
			String PV_REQUEST_PWD, String PV_LOG_REPOSITORY, int PV_TEST_CALL_LEVEL, int PV_TRACE_LEVEL ,   
			String PV_ERROR_CODE, int PV_ERROR_SEVERITY, String PV_ERROR_MESSAGE) {
		
		// create a list of IN parameters with their corresponding indices in the order specified by the procedure
		LinkedHashMap<Integer, ValueDatatypePair> inParam = new LinkedHashMap<Integer, ValueDatatypePair>();
		inParam.put(1, new ValueDatatypePair(PV_MAIN_OPERATION, Types.VARCHAR));
		inParam.put(2, new ValueDatatypePair(PV_ACCT_NO, Types.VARCHAR));
		inParam.put(3, new ValueDatatypePair(PV_CUST_NO, Types.VARCHAR));
		inParam.put(4, new ValueDatatypePair(PV_CARD_PAN, Types.VARCHAR));
		inParam.put(5, new ValueDatatypePair(PV_DELIVERY_TYPE, Types.VARCHAR));
		inParam.put(6, new ValueDatatypePair(PV_DELIVERY_ADDRESS, Types.VARCHAR));
		inParam.put(7, new ValueDatatypePair(PV_REC_ST, Types.VARCHAR));
		inParam.put(8, new ValueDatatypePair(PV_VERSION_NO, Types.INTEGER));
		inParam.put(9, new ValueDatatypePair(PV_ROW_TS, Types.DATE));
		inParam.put(10, new ValueDatatypePair(PV_USER_ID, Types.VARCHAR));
		inParam.put(11, new ValueDatatypePair(PV_CREATE_DT, Types.DATE));
		inParam.put(12, new ValueDatatypePair(PV_SYS_CREATE_TS, Types.DATE));
		inParam.put(13, new ValueDatatypePair(PV_CREATED_BY, Types.VARCHAR));
		inParam.put(14, new ValueDatatypePair(PV_REQUEST_PWD, Types.VARCHAR));
		inParam.put(15, new ValueDatatypePair(PV_LOG_REPOSITORY, Types.VARCHAR));
		inParam.put(16, new ValueDatatypePair(PV_TEST_CALL_LEVEL, Types.INTEGER));
		inParam.put(17, new ValueDatatypePair(PV_TRACE_LEVEL, Types.INTEGER));
		inParam.put(18, new ValueDatatypePair(PV_ERROR_CODE, Types.VARCHAR));
		inParam.put(19, new ValueDatatypePair(PV_ERROR_SEVERITY, Types.INTEGER));
		inParam.put(20, new ValueDatatypePair(PV_ERROR_MESSAGE, Types.VARCHAR));

		// create a list of OUT parameters with their corresponding indices in the order specified by the procedure
		LinkedHashMap<Integer, ValueDatatypePair> outParam = new LinkedHashMap<Integer, ValueDatatypePair>();
		outParam.put(15, new ValueDatatypePair(null, Types.VARCHAR));
		outParam.put(16, new ValueDatatypePair(null, Types.INTEGER));
		outParam.put(17, new ValueDatatypePair(null, Types.INTEGER));
		outParam.put(18, new ValueDatatypePair(null, Types.VARCHAR));
		outParam.put(19, new ValueDatatypePair(null, Types.INTEGER));
		outParam.put(20, new ValueDatatypePair(null, Types.VARCHAR));

		// execute the procedure with it's parameters
		LinkedHashMap<Integer, ValueDatatypePair> resultParam = executeProcedure("NSP_CARD_REPLACEMENT", inParam, outParam);

		// procedure executed successfully if error severity is null or empty
		// that's the reason for initializing error severity
		String errorSeverity = "-1", errorMessage = "";
		if (resultParam.get(0).getValue().equals(ResponseConstants.SUCCEESS_CODE)) {
			errorSeverity = resultParam.get(19).getValue().equals(null) ? "" : resultParam.get(19).getValue();
			errorMessage = resultParam.get(20).getValue();
		}
		
		ResponseModel responseModel = new ResponseModel();
		responseModel.setResponseCode(ResponseConstants.PROCEDURE_CODE);
		responseModel.setResponseMessage(ResponseConstants.PROCEDURE_MESSAGE + errorMessage);

		if (errorSeverity.isEmpty() || Integer.valueOf(errorSeverity) == 0) {
			responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}
		
		return responseModel;
	}
	
	public int saveRequest(String reference, String customerNumber, String accountNumber, String cardPan, 
			String narration, String trans_type, String responseCd) {

		String sql = "INSERT INTO  "
				+ "ALT_QTELLER_MB_CARD(TRAN_REF, CUST_NO, ACCT_NO, CARD_PAN, NARRATION, SUB_TRAN_REF, RESPONSE_CD) "
				+ " VALUES(?,?,?,?,?,?,?)";

		// create parameter list in order received by the query
		List<ValueDatatypePair> param = new ArrayList<ValueDatatypePair>();
		param.add(new ValueDatatypePair(reference, Types.VARCHAR));
		param.add(new ValueDatatypePair(customerNumber, Types.VARCHAR));
		param.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));
		param.add(new ValueDatatypePair(cardPan, Types.VARCHAR));
		param.add(new ValueDatatypePair(narration, Types.VARCHAR));
		param.add(new ValueDatatypePair(trans_type, Types.VARCHAR));
		param.add(new ValueDatatypePair(responseCd, Types.VARCHAR));

		return executeDML(sql, param);
	}
	
	
	
}
