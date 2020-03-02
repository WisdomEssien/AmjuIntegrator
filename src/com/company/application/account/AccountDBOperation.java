package com.company.application.account;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.company.application.account.data.AccountHistory;
import com.company.application.account.data.AccountResponse;
import com.company.application.account.data.Beneficiary;
import com.company.application.account.data.MiniStatement;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Database.DBConnection;
import com.neptunesoftware.reuseableClasses.Database.ValueDatatypePair;

public class AccountDBOperation extends DBConnection{

	
	public AccountDBOperation() {
		super();
	}
	
	public AccountDBOperation(final String databaseName) {
		super(databaseName);
	}
		
	public AccountDBOperation(final String driver, final String connectionURL, final String username, final String password, final String databaseType) {
		super(driver, connectionURL, username, password, databaseType);
	}
	
	
	public List<MiniStatement> selectMiniStatement(final String accountNo){
				
		String sql = "SELECT TO_CHAR(SYS_CREATE_TS, 'DD/MM/YYYY') \"DATE\", DR_CR_IND CR_DR, TXN_AMT AMOUNT, TRAN_REF_TXT REF_NO \r\n"
				+ "FROM DEPOSIT_ACCOUNT_HISTORY \r\n"
				+ "WHERE TO_CHAR(SYS_CREATE_TS, 'MM') = TO_CHAR(SYSDATE, 'MM') \r\n"
				+ "AND DEPOSIT_ACCT_ID = (SELECT ACCT_ID FROM ACCOUNT WHERE ACCT_NO = ?)";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNo, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		List<MiniStatement> miniStatementLst = new ArrayList<MiniStatement>();
		
		// Loop through each row returned
		for (Map.Entry<Integer, HashMap<String, String>> rowEntry : records.entrySet()) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			MiniStatement miniStatement = new MiniStatement();
			miniStatement.setDate(rowEntry.getValue().get("DATE".toUpperCase()));
			miniStatement.setCreditDebit(rowEntry.getValue().get("CR_DR".toUpperCase()));
			miniStatement.setAmount(rowEntry.getValue().get("amount".toUpperCase()));
			miniStatement.setRefNo(rowEntry.getValue().get("REF_NO".toUpperCase()));

			miniStatementLst.add(miniStatement);
		}

		return miniStatementLst;
	}
	
	public List<AccountResponse> selectMultiAccount(final String accountNo) {

		String sql = "SELECT T1.CUST_ID, T2.ACCT_NO, T2.ACCT_NM,T2.REC_ST,T2.PROD_CAT_TY, T3.LEDGER_BAL, T4.CRNCY_CD_ISO,T5.CONTACT, T7.ACCESSPIN,T8.REF_DESC\r\n"
				+ "FROM CUSTOMER T1 join ACCOUNT T2 on T1.CUST_ID = T2.CUST_ID\r\n"
				+ "join DEPOSIT_ACCOUNT_SUMMARY T3 on T2.ACCT_ID = T3.DEPOSIT_ACCT_ID\r\n"
				+ "join CURRENCY T4 on T2.CRNCY_ID = T4.CRNCY_ID AND T4.CRNCY_CD_ISO = 'NGN'\r\n"
				+ "left join CUSTOMER_CONTACT_MODE T5 on T1.CUST_ID = T5.CUST_ID AND REGEXP_LIKE (T5.CONTACT, '^(\\+|[0-9])') \r\n"
				+ "left join CONTACT_MODE_REF T6 on T5.CONTACT_MODE_ID = T6.CONTACT_MODE_ID\r\n"
				+ "and T6.CONTACT_MODE_ID IN (237,231,236) left join ALT_MAPP_DEVICE T7 on T2.ACCT_NO = T7.ACCT_NUM\r\n"
				+ "left join PRODUCT_CATEGORY_REF T8 on T8.REF_KEY = T2.PROD_CAT_TY\r\n"
				+ "where T1.CUST_ID IN (SELECT CUST_ID FROM ACCOUNT WHERE ACCT_NM =\r\n"
				+ "(SELECT ACCT_NM FROM ACCOUNT WHERE ACCT_NO = ?))\r\n" +
				// "AND T1.CUST_NM = (SELECT ACCT_NM FROM ACCOUNT WHERE ACCT_NO = ?)\r\n" +
				"order by 8 desc";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNo, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		List<AccountResponse> accounts = new ArrayList<AccountResponse>();
		
		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			AccountResponse nameInquiry = new AccountResponse();

			nameInquiry.setAccountNumber(records.get(rowIndex).get("ACCT_NO".toUpperCase()));
			nameInquiry.setAccountName(records.get(rowIndex).get("ACCT_NM".toUpperCase()));
			nameInquiry.setAccountStatus(records.get(rowIndex).get("REC_ST".toUpperCase()));
			nameInquiry.setAccountType(records.get(rowIndex).get("REF_DESC".toUpperCase()));
			nameInquiry.setLedgerBalance(records.get(rowIndex).get("LEDGER_BAL".toUpperCase()));
			nameInquiry.setCurrencyCode(records.get(rowIndex).get("CRNCY_CD_ISO".toUpperCase()));
			nameInquiry.setPhoneNumber(records.get(rowIndex).get("CONTACT".toUpperCase()));
			nameInquiry.setAccessPin(records.get(rowIndex).get("ACCESSPIN".toUpperCase()));

			accounts.add(nameInquiry);
		}

		return accounts;
	}
	
	public List<AccountHistory> selectAccountHistory(final String accountNo, final String startDate, final String endDate) {
			
//		String sql = "select T2.PROD_CAT_TY,T3.REF_DESC,T1.tran_desc, T1.txn_amt, T1.stmnt_bal, T1.sys_create_ts,to_char(T1.sys_create_ts,'dd-MON-yyyy') txndate \r\n"
//				+ "from deposit_account_history T1 join ACCOUNT T2 on T1.acct_no = T2.acct_no \r\n"
//				+ "left join PRODUCT_CATEGORY_REF T3 on T3.REF_KEY = T2.PROD_CAT_TY\r\n" + "where t1.acct_no = ?  \r\n"
//				+ "order by sys_create_ts desc";
		
		String sql = "select T2.PROD_CAT_TY,T3.REF_DESC,T1.tran_desc, T1.txn_amt, T1.stmnt_bal, T1.sys_create_ts,to_char(T1.sys_create_ts,'dd-MON-yyyy') txndate\r\n" + 
				"from deposit_account_history T1 join ACCOUNT T2 on T1.acct_no = T2.acct_no\r\n" + 
				"left join PRODUCT_CATEGORY_REF T3 on T3.REF_KEY = T2.PROD_CAT_TY \r\n" + 
				"where t1.acct_no = ? \r\n" + 
				"and T1.sys_create_ts >= trunc(to_date(?, 'yyyy-MM-dd') - interval '1' month, 'month')\r\n" + 
				"and T1.sys_create_ts <= to_date(?, 'yyyy-MM-dd')\r\n" + 
				"order by sys_create_ts desc";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNo, Types.VARCHAR));
		params.add(new ValueDatatypePair(startDate, Types.VARCHAR));
		params.add(new ValueDatatypePair(endDate, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		List<AccountHistory> accountHistory = new ArrayList<AccountHistory>();

		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			AccountHistory acctHist = new AccountHistory();

			acctHist.setAccountType(records.get(rowIndex).get("REF_DESC".toUpperCase()));
			acctHist.setTransactionDesc(records.get(rowIndex).get("tran_desc".toUpperCase()));
			acctHist.setTransactionAmount(records.get(rowIndex).get("txn_amt".toUpperCase()));
			acctHist.setBalanceAfter(records.get(rowIndex).get("stmnt_bal".toUpperCase()));
			acctHist.setTransactionDate(records.get(rowIndex).get("txndate".toUpperCase()));

			accountHistory.add(acctHist);
		}

		return accountHistory;
		
	}
	
	public List<Beneficiary> selectBeneficiary(final String senderAcctNo, final String moduleId) {

		String sql = "select Ben_Id, Ben_Acct_No, Ben_Acct_Name, Ben_Bank_Code, Ben_Bank_Name\r\n"
				+ "from alt_quickteller_beneficiary\r\n" + "where Sender_Acct_No = ? and Module_Id = ?";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(senderAcctNo, Types.VARCHAR));
		params.add(new ValueDatatypePair(moduleId, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		List<Beneficiary> beneficiaries = new ArrayList<Beneficiary>();

		// must be included based on Vincent's request
		// beneficiaryList.add(new Beneficiary("0", "null", "Choose Beneficiary", "null", "null"));

		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			Beneficiary beneficiary = new Beneficiary();

			beneficiary.setBeneficiaryId(records.get(rowIndex).get("Ben_Id".toUpperCase()));
			beneficiary.setBeneficiaryAcctNo(records.get(rowIndex).get("Ben_Acct_No".toUpperCase()));
			beneficiary.setBeneficiaryAcctName(records.get(rowIndex).get("Ben_Acct_Name".toUpperCase()));
			beneficiary.setBankCode(records.get(rowIndex).get("Ben_Bank_Code".toUpperCase()));
			beneficiary.setBankName(records.get(rowIndex).get("Ben_Bank_Name".toUpperCase()));

			beneficiaries.add(beneficiary);
		}

		return beneficiaries;
	}
	
	public AccountResponse selectAccountInfo(final String accountNo) {
		
		String sql = "SELECT T2.ACCT_NO, T2.ACCT_NM,T2.REC_ST,T2.PROD_CAT_TY, T3.LEDGER_BAL, T4.CRNCY_CD_ISO,T5.CONTACT, T7.ACCESSPIN "
				+ "FROM CUSTOMER T1 join ACCOUNT T2 on T1.CUST_ID = T2.CUST_ID "
				+ "join DEPOSIT_ACCOUNT_SUMMARY T3 on T2.ACCT_ID = T3.DEPOSIT_ACCT_ID "
				+ "join CURRENCY T4 on T2.CRNCY_ID = T4.CRNCY_ID "
				+ "left join CUSTOMER_CONTACT_MODE T5 on T1.CUST_ID = T5.CUST_ID "
				+ "left join CONTACT_MODE_REF T6 on T5.CONTACT_MODE_ID = T6.CONTACT_MODE_ID "
				+ "and T6.CONTACT_MODE_ID IN (237,231,236) left join ALT_MAPP_DEVICE T7 on T2.ACCT_NO = T7.ACCT_NUM "
				+ " where T2.ACCT_NO = ? order by 7 desc";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNo, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		AccountResponse accountResponse = new AccountResponse();
		accountResponse.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
		accountResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
		
		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			accountResponse.setAccountNumber(records.get(rowIndex).get("ACCT_NO".toUpperCase()));
			accountResponse.setAccountName(records.get(rowIndex).get("ACCT_NM".toUpperCase()));
			accountResponse.setAccountStatus(records.get(rowIndex).get("REC_ST".toUpperCase()));
			accountResponse.setAccountType(records.get(rowIndex).get("PROD_CAT_TY".toUpperCase()));
			accountResponse.setLedgerBalance(records.get(rowIndex).get("LEDGER_BAL".toUpperCase()));
			accountResponse.setCurrencyCode(records.get(rowIndex).get("CRNCY_CD_ISO".toUpperCase()));
			accountResponse.setPhoneNumber(records.get(rowIndex).get("CONTACT".toUpperCase()));
			accountResponse.setAccessPin(records.get(rowIndex).get("ACCESSPIN".toUpperCase()));

			accountResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			accountResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}

		return accountResponse;
	}
	
	public String selectDailyTranxDone(final String accountNo, final String transactionMethod) {

		String sql = "select nvl(sum(tran_amount),0) txn_done \r\n" + "from alt_quickteller\r\n"
				+ "where from_acct_num = ? and tran_nethod = ? \r\n"
				+ "and to_char(system_ts,'dd-MON-yyyy') = to_char(sysdate,'dd-MON-yyyy')";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNo, Types.VARCHAR));
		params.add(new ValueDatatypePair(transactionMethod, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		String doneTransaction = "0";

		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			doneTransaction = records.get(rowIndex).get("txn_done".toUpperCase());
		}

		return doneTransaction;		
	}
	
	public String accountName(final String accountNo) {

		String sql = "select acct_no, acct_nm from account \r\n" + "where acct_no = ?";
		
		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNo, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		String accountName = "";

		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			accountName = records.get(rowIndex).get("acct_nm".toUpperCase());
		}

		return accountName;
	}
	
	public ResponseModel deleteBeneficiary(final String senderAcctNo, final String moduleId, final int beneficiaryId){
		
		String sql = " delete from alt_quickteller_beneficiary\r\n" + 
				" where Ben_Id = ?";
		
		// input parameters in the order needed in the query
		List<ValueDatatypePair> inParam = new ArrayList<ValueDatatypePair>();
		inParam.add(new ValueDatatypePair(beneficiaryId, Types.INTEGER));
		
		int numOfRows = executeDML(sql, inParam);
		
		if (beneficiaryId == 0) {
			sql = " delete from alt_quickteller_beneficiary where Sender_Acct_No = ? "
					+ "and Module_id = ?";

			List<ValueDatatypePair> inParameter = new ArrayList<ValueDatatypePair>();
			inParameter.add(new ValueDatatypePair(senderAcctNo, Types.VARCHAR));
			inParameter.add(new ValueDatatypePair(moduleId, Types.VARCHAR));
			
			numOfRows = executeDML(sql, inParameter);
		}
		
		ResponseModel responseModel = new ResponseModel();
		responseModel.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
		responseModel.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
		
		if(numOfRows >= 0) {
			System.out.println("Beneficiary delete successful");
		
			responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}
				
		return responseModel;
    } 
	
	public ResponseModel saveBeneficiary(String moduleId, String senderAcctNo, String beneficiaryAcctNo, String beneficiaryAcctName,
    							String beneficiaryBankCode, String beneficiaryBankName) {
    	
		// query to check whether beneficiary has been saved previously
		String sql = "select Ben_Id, Ben_Acct_No, Ben_Acct_Name, Ben_Bank_Code, Ben_Bank_Name\r\n"
				+ "from alt_quickteller_beneficiary\r\n"
				+ "where Sender_Acct_No = ? and Module_Id = ? and Ben_Acct_No = ?";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(senderAcctNo, Types.VARCHAR));
		params.add(new ValueDatatypePair(moduleId, Types.VARCHAR));
		params.add(new ValueDatatypePair(beneficiaryAcctNo, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		ResponseModel responseModel = new ResponseModel();

		// Null is returned when an exception is thrown.
		if (records == null) {
			responseModel.setResponseCode(ResponseConstants.EXCEPTION_CODE);
			responseModel.setResponseMessage(ResponseConstants.EXCEPTION_MESSAGE);

			return responseModel;
		}

		// return already exist
		if (!records.isEmpty()) {
			responseModel.setResponseCode(ResponseConstants.ALREADY_EXIST_CODE);
			responseModel.setResponseMessage(ResponseConstants.ALREADY_EXIST_MESSAGE);			
		}
		
		// save when beneficiary does not exist
		if (records.isEmpty()) {

			String query = "insert into \r\n"
					+ "alt_quickteller_beneficiary(Module_id, Sender_Acct_No, Ben_Acct_No, Ben_Acct_Name, Ben_Bank_Code, Ben_Bank_Name)\r\n"
					+ "values(?,?,?,?,?,?)";

			List<ValueDatatypePair> inParameter = new ArrayList<ValueDatatypePair>();
			inParameter.add(new ValueDatatypePair(moduleId, Types.VARCHAR));
			inParameter.add(new ValueDatatypePair(senderAcctNo, Types.VARCHAR));
			inParameter.add(new ValueDatatypePair(beneficiaryAcctNo, Types.VARCHAR));
			inParameter.add(new ValueDatatypePair(beneficiaryAcctName, Types.VARCHAR));
			inParameter.add(new ValueDatatypePair(beneficiaryBankCode, Types.VARCHAR));
			inParameter.add(new ValueDatatypePair(beneficiaryBankName, Types.VARCHAR));

			executeDML(query, inParameter);
			
			responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}

		return responseModel;
	}
	
}
