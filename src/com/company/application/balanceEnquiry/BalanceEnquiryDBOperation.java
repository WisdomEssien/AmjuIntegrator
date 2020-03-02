package com.company.application.balanceEnquiry;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.neptunesoftware.reuseableClasses.Database.DBConnection;
import com.neptunesoftware.reuseableClasses.Database.ValueDatatypePair;

public class BalanceEnquiryDBOperation extends DBConnection {

	public BalanceEnquiryDBOperation() {
		super();
	}
	
	public BalanceEnquiryDBOperation(String databaseName) {
		super(databaseName);
	}
		
	public BalanceEnquiryDBOperation(String driver, String connectionURL, String username, String password, String databaseType) {
		super(driver, connectionURL, username, password, databaseType);
	}
	
	
	
	public String accountBalance(String accountNo) {

		String sql = "SELECT SUM(  DAS.CLEARED_BAL\r\n" + "        + NVL (\r\n"
				+ "              (SELECT SUM (CREDIT_APPL_OD_INFO_LIMIT.APPROVED_AMT)\r\n"
				+ "                 FROM CREDIT_APPL_OD_INFO, CREDIT_APPL_OD_INFO_LIMIT\r\n"
				+ "                WHERE     CREDIT_APPL_OD_INFO.CREDIT_APPL_OD_INFO_ID =\r\n"
				+ "                          CREDIT_APPL_OD_INFO_LIMIT.CREDIT_APPL_OD_INFO_ID\r\n"
				+ "                      AND DAS.DEPOSIT_ACCT_ID =\r\n"
				+ "                          CREDIT_APPL_OD_INFO.DEPOSIT_ACCT_ID\r\n"
				+ "                      AND CREDIT_APPL_OD_INFO.EXPIRY_DT >\r\n"
				+ "                          (SELECT TO_DATE (DISPLAY_VALUE, 'DD/MM/YYYY')\r\n"
				+ "                             FROM CTRL_PARAMETER\r\n"
				+ "                            WHERE PARAM_CD = 'S02')),\r\n" + "              0)\r\n"
				+ "        - NVL (\r\n" + "              (SELECT SUM (TXN.TXN_AMT)\r\n"
				+ "                 FROM DEPOSIT_ACCOUNT_HISTORY TXN\r\n"
				+ "                WHERE     TXN.ACCT_NO = DAS.ACCT_NO\r\n"
				+ "                      AND TXN.VALUE_DT >\r\n"
				+ "                          (SELECT TO_DATE (DISPLAY_VALUE, 'DD/MM/YYYY')\r\n"
				+ "                             FROM CTRL_PARAMETER\r\n"
				+ "                            WHERE PARAM_CD = 'S02')\r\n"
				+ "                      AND TXN.DR_CR_IND = 'CR'\r\n"
				+ "                      AND TXN.CHANNEL_ID NOT IN '5'),\r\n" + "              0)\r\n"
				+ "        - NVL ((SELECT RESERVED_FUND\r\n" + "                  FROM DEPOSIT_ACCOUNT_SUMMARY\r\n"
				+ "                 WHERE ACCT_NO = DAS.ACCT_NO),\r\n" + "               0)\r\n"
				+ "        - NVL ((SELECT EARMARKED_FUND\r\n" + "                  FROM DEPOSIT_ACCOUNT_SUMMARY\r\n"
				+ "                 WHERE ACCT_NO = DAS.ACCT_NO),\r\n" + "               0)\r\n"
				+ "        - NVL ((SELECT CUMULATIVE_LIEN_AMT\r\n"
				+ "                  FROM DEPOSIT_ACCOUNT_SUMMARY\r\n"
				+ "                 WHERE ACCT_NO = DAS.ACCT_NO),\r\n" + "               0)\r\n" + "        - NVL (\r\n"
				+ "              (SELECT SUM (ARN.RES_VALUE)\r\n" + "                 FROM ACCOUNT_RESTRICTION ARN\r\n"
				+ "                WHERE     ACCT_ID = DAS.DEPOSIT_ACCT_ID\r\n"
				+ "                      AND ARN.REC_ST = 'A'\r\n"
				+ "                      AND ARN.RES_SUB_TY_CD NOT IN ('PRDRES49')),\r\n" + "              0)\r\n"
				+ "        - NVL (\r\n" + "              (SELECT SUM (TXN.TRAN_AMT)\r\n"
				+ "                 FROM TXN_JOURNAL TXN\r\n"
				+ "                WHERE     TXN.ACCT_NO = DAS.ACCT_NO\r\n"
				+ "                      AND TXN.REC_ST = 'U'\r\n"
				+ "                      AND TXN.DR_CR_IND = 'DR'),\r\n" + "              0)\r\n"
				+ "        - NVL ((SELECT DR_INT_ACCRUED\r\n" + "                  FROM DEPOSIT_ACCOUNT_SUMMARY\r\n"
				+ "                 WHERE ACCT_NO = DAS.ACCT_NO),\r\n" + "               0)\r\n" + "        - NVL (\r\n"
				+ "              (SELECT SUM (EJ.ACTUAL_CHRG_AMT)\r\n"
				+ "                 FROM EVENT_PENDING_CHARGE_JOURNAL EJ\r\n"
				+ "                WHERE     EJ.CHRG_SETLMNT_ACCT_ID = DAS.DEPOSIT_ACCT_ID\r\n"
				+ "                      AND EJ.REC_ST = 'P'),\r\n" + "              0)\r\n" + "        - NVL (\r\n"
				+ "              (SELECT SUM (L.AMT_UNPAID)\r\n"
				+ "                 FROM LN_ACCT_REPMNT_EVENT        L,\r\n"
				+ "                      DEPOSIT_ACCOUNT             DA,\r\n"
				+ "                      DEPOSIT_PRODUCT_BASIC_INFO  D\r\n"
				+ "                WHERE     L.FUNDING_ACCT_ID = DAS.DEPOSIT_ACCT_ID\r\n"
				+ "                      AND L.REC_ST NOT IN ('C', 'S')\r\n"
				+ "                      AND L.FUNDING_ACCT_ID = DA.DEPOSIT_ACCT_ID\r\n"
				+ "                      AND DA.PROD_ID = D.PROD_ID\r\n"
				+ "                      AND D.LOAN_REPMNT_RESRV_FUNDS_FG = 'Y'\r\n"
				+ "                      AND L.DUE_DT <=\r\n"
				+ "                          (SELECT TO_DATE (DISPLAY_VALUE, 'DD/MM/YYYY')\r\n"
				+ "                             FROM CTRL_PARAMETER\r\n"
				+ "                            WHERE PARAM_CD = 'S02')),\r\n" + "              0)) AVAILABLE_BAL\r\n"
				+ "  FROM DEPOSIT_ACCOUNT_SUMMARY DAS\r\n" + " WHERE DAS.ACCT_NO = ?";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNo, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return "";
		}

		String availableBalance = "0";

		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			availableBalance = records.get(rowIndex).get("AVAILABLE_BAL".toUpperCase());
		}

		return availableBalance;
	}
	
	
}
