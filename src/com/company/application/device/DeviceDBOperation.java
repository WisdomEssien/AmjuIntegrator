package com.company.application.device;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.company.application.device.data.DeviceLoginResponse;
import com.company.application.device.data.Notification;
import com.company.application.device.data.RegisterDeviceResponse;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Database.DBConnection;
import com.neptunesoftware.reuseableClasses.Database.ValueDatatypePair;

public class DeviceDBOperation extends DBConnection{

	public DeviceDBOperation() {
		super();
	}
	
	public DeviceDBOperation(String databaseName) {
		super(databaseName);
	}
	
	public DeviceDBOperation(String driver, String connectionURL, String username, String password, String databaseType) {
		super(driver, connectionURL, username, password, databaseType);
	}
	
	
	public List<Notification> selectNotification() {

		String sql = "select id, message, to_char(start_dt, 'dd-MM-yyyy') start_date, to_char(end_dt, 'dd-MM-yyyy') end_date\r\n"
				+ "from alt_notification\r\n" + "where start_dt < sysdate\r\n"
				+ "and to_date(to_char(end_dt, 'dd-MM-yyyy'), 'dd-MM-yyyy') >= to_date(to_char(sysdate, 'dd-MM-yyyy'), 'dd-MM-yyyy')\r\n"
				+ "order by id desc";

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		List<Notification> notifications = new ArrayList<Notification>();

		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			Notification notification = new Notification();
			notification.setId(Integer.valueOf(records.get(rowIndex).get("id".toUpperCase())));
			notification.setMessage(records.get(rowIndex).get("message".toUpperCase()));
			notification.setStartDate(records.get(rowIndex).get("start_date".toUpperCase()));
			notification.setEndDate(records.get(rowIndex).get("end_date".toUpperCase()));

			notifications.add(notification);
		}

		return notifications;
	}
	
	public DeviceLoginResponse selectLoginDetails(final String accountNumber, final String password, final String deviceUIID) {

		String sql = " SELECT A.ACCT_NUM, A.PHONE_NUM, A.DEVICE_UIID, A.PASSWORD, A.ACCESSPIN , MAX(B.SYSTEM_TS) lastLogin "
				+ " FROM ALT_MAPP_DEVICE A, ALT_MAPP_LOGIN_HISTORY B	"
				+ " WHERE A.ACCT_NUM = B.ACCT_NUM(+) AND A.DEVICE_UIID = B.DEVICE_UIID(+) "
				+ " AND A.ACCT_NUM =? AND A.PASSWORD = ? AND A.DEVICE_UIID = ? "
				+ " GROUP BY A.ACCT_NUM, A.PHONE_NUM, A.DEVICE_UIID, A.PASSWORD, A.ACCESSPIN";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));
		params.add(new ValueDatatypePair(password, Types.VARCHAR));
		params.add(new ValueDatatypePair(deviceUIID, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		DeviceLoginResponse deviceLoginResponse = new DeviceLoginResponse();
		deviceLoginResponse.setResponseCode(ResponseConstants.LOGIN_CODE);
		deviceLoginResponse.setResponseMessage(ResponseConstants.LOGIN_MESSAGE);

		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			deviceLoginResponse.setAccountNumber(records.get(rowIndex).get("ACCT_NUM".toUpperCase()));
			deviceLoginResponse.setPhoneNo(records.get(rowIndex).get("PHONE_NUM".toUpperCase()));
			deviceLoginResponse.setDeviceUIID(records.get(rowIndex).get("DEVICE_UIID".toUpperCase()));
			deviceLoginResponse.setPassword(records.get(rowIndex).get("PASSWORD".toUpperCase()));
			deviceLoginResponse.setAccessPin(records.get(rowIndex).get("ACCESSPIN".toUpperCase()));
			deviceLoginResponse.setLastlogin(records.get(rowIndex).get("lastLogin".toUpperCase()));

			deviceLoginResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			deviceLoginResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
			
			saveCurrentLoginTime(accountNumber, deviceUIID);
		}

		return deviceLoginResponse;
	}
	
	private ResponseModel saveCurrentLoginTime(final String accountNumber,final String deviceUIID) {
		
		String sql = "INSERT INTO  \r\n" + 
				" ALT_MAPP_LOGIN_HISTORY(ACCT_NUM,DEVICE_UIID,LOGIN_DATE) \r\n" + 
				" VALUES(?,?,?)";

		// create parameter list in the order needed by the query
		List<ValueDatatypePair> inParam = new ArrayList<ValueDatatypePair>();
		inParam.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));
		inParam.add(new ValueDatatypePair(deviceUIID, Types.VARCHAR));
		inParam.add(new ValueDatatypePair(getCurrentDate(), Types.VARCHAR));
		
		int numOfRow = executeDML(sql, inParam);
		
		ResponseModel responseModel = new ResponseModel();
		responseModel.setResponseCode(ResponseConstants.QUERY_CODE);
		responseModel.setResponseMessage(ResponseConstants.QUERY_MESSAGE);
		
		if (numOfRow >= 1) {
			responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}
		
		return responseModel;		
	}
	
	public DeviceLoginResponse selectLoginDetails(final String accountNumber, final String deviceUIID) {

		String sql = "SELECT ACCT_NUM, PHONE_NUM, DEVICE_UIID, PASSWORD, ACCESSPIN FROM ALT_MAPP_DEVICE "
				+ " WHERE ACCT_NUM = ? AND DEVICE_UIID = ?";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));
		params.add(new ValueDatatypePair(deviceUIID, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		DeviceLoginResponse deviceLoginResponse = new DeviceLoginResponse();
		deviceLoginResponse.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
		deviceLoginResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);

		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			deviceLoginResponse.setAccountNumber(records.get(rowIndex).get("ACCT_NUM".toUpperCase()));
			deviceLoginResponse.setPhoneNo(records.get(rowIndex).get("PHONE_NUM".toUpperCase()));
			deviceLoginResponse.setDeviceUIID(records.get(rowIndex).get("DEVICE_UIID".toUpperCase()));
			deviceLoginResponse.setPassword(records.get(rowIndex).get("PASSWORD".toUpperCase()));
			deviceLoginResponse.setAccessPin(records.get(rowIndex).get("ACCESSPIN".toUpperCase()));

			deviceLoginResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			deviceLoginResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}

		return deviceLoginResponse;
	}
	
	public boolean isAnotherDevice(final String accountNumber, final String deviceUIID) {

		String sql = "SELECT ACCT_NUM, PHONE_NUM, DEVICE_UIID, PASSWORD, ACCESSPIN FROM ALT_MAPP_DEVICE "
				+ " WHERE ACCT_NUM = ? ";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);
		
		// Null is returned when an exception is thrown.
		if (records == null) {
			return false;
		}

		// for new users, set to false
		boolean response = records.size() > 0 ? true : false;
		
		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			String previousDeviceUIID = records.get(rowIndex).get("DEVICE_UIID".toUpperCase());
			
			if(previousDeviceUIID.equals(deviceUIID))
				response =  false;
		}
		
		return response;
	}
	
	public RegisterDeviceResponse register(final String accountNumber, final String phoneNo, final String deviceUIID, final String password, final String accessPin) {
		
		// delete previous registration
		String query = "DELETE FROM  ALT_MAPP_DEVICE WHERE ACCT_NUM = ?";

		List<ValueDatatypePair> inParam = new ArrayList<ValueDatatypePair>();
		inParam.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));

		executeDML(query, inParam);

		// create new registration
		String sql = "INSERT INTO  "
				+ " ALT_MAPP_DEVICE(ACCT_NUM, PHONE_NUM, DEVICE_UIID, PASSWORD, ACCESSPIN, REG_DATE) "
				+ " VALUES(?,?,?,?,?,?)";

		// create parameter list in the order needed by the query
		List<ValueDatatypePair> inParameter = new ArrayList<ValueDatatypePair>();
		inParameter.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));
		inParameter.add(new ValueDatatypePair(phoneNo, Types.VARCHAR));
		inParameter.add(new ValueDatatypePair(deviceUIID, Types.VARCHAR));
		inParameter.add(new ValueDatatypePair(password, Types.VARCHAR));
		inParameter.add(new ValueDatatypePair(accessPin, Types.VARCHAR));
		inParameter.add(new ValueDatatypePair(getCurrentDate(), Types.DATE));
		
		int numOfRow = executeDML(sql, inParameter);
		
		RegisterDeviceResponse registerDeviceResponse = new RegisterDeviceResponse();
		registerDeviceResponse.setResponseCode(ResponseConstants.QUERY_CODE);
		registerDeviceResponse.setResponseMessage(ResponseConstants.QUERY_MESSAGE);
		
		if (numOfRow >= 1) {
			registerDeviceResponse.setAccountNumber(accountNumber);
			registerDeviceResponse.setPhoneNo(phoneNo);
			registerDeviceResponse.setDeviceUIID(deviceUIID);
			registerDeviceResponse.setPassword(password);
			registerDeviceResponse.setAccessPin(accessPin);

			registerDeviceResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			registerDeviceResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}
		
		return registerDeviceResponse;
		
	}
	
	public ResponseModel saveOTP(final String phoneNo, final String otp) {
		
		String sql = "INSERT INTO  "
				+ " ALT_MAPP_DEVICE_OTP(PHONE_NUM,OTP) "
				+ " VALUES(?,?)";

		// create parameter list in the order needed by the query
		List<ValueDatatypePair> inParam = new ArrayList<ValueDatatypePair>();
		inParam.add(new ValueDatatypePair(phoneNo, Types.VARCHAR));
		inParam.add(new ValueDatatypePair(otp, Types.VARCHAR));
		
		int numOfRow = executeDML(sql, inParam);
		
		ResponseModel responseModel = new ResponseModel();
		responseModel.setResponseCode(ResponseConstants.QUERY_CODE);
		responseModel.setResponseMessage(ResponseConstants.QUERY_MESSAGE);
		
		if (numOfRow >= 1) {
			responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}
		
		return responseModel;		
	}
	
	public ResponseModel saveSMS(String accountNo, String phoneNo, String message, String smsType) {
		
		String sql = "INSERT INTO ALT_MAPP_SMS(ACCT_NO, RECIPIENT_PHONE_NUMBER, MESSAGE_CONTENT, SMS_TYPE) " + 
				" VALUES(?,?,?,?)";

		// create parameter list in the order needed by the query
		List<ValueDatatypePair> inParam = new ArrayList<ValueDatatypePair>();
		inParam.add(new ValueDatatypePair(accountNo, Types.VARCHAR));
		inParam.add(new ValueDatatypePair(phoneNo, Types.VARCHAR));
		inParam.add(new ValueDatatypePair(message, Types.VARCHAR));
		inParam.add(new ValueDatatypePair(smsType, Types.VARCHAR));
		
		int numOfRow = executeDML(sql, inParam);
		
		ResponseModel responseModel = new ResponseModel();
		responseModel.setResponseCode(ResponseConstants.QUERY_CODE);
		responseModel.setResponseMessage(ResponseConstants.QUERY_MESSAGE);
		
		if (numOfRow >= 1) {
			responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}
		
		return responseModel;		
	}
	
	
	public List<Long> selectUserRoles(String username) {

		String sql = "SELECT USER_ROLE_ID FROM USER_ROLE WHERE SYSUSER_ID = (SELECT SYSUSER_ID \r\n" + 
				" FROM SYSUSER WHERE LOGIN_ID = ?)";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(username, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		// Null is returned when an exception is thrown.
		if (records == null) {
			return null;
		}

		List<Long> userRoleIds = new ArrayList<Long>();

		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			userRoleIds.add(Long.valueOf(records.get(rowIndex).get("USER_ROLE_ID".toUpperCase())));
		}

		return userRoleIds;
	}
	
	public int generateAndSendAlert(long userRoleId, long custId, int remainingDays) {
		long alertId = nextRecordIdFromTable("ALERT");
		String alertRefNo = "MAPP Lincence/" + alertId;
		String alertSubject = "Mobile App channel Licence Expiration";
		String alertSubTypeCode = "Countdown to Mobile App licence expiration date";
		String alertDetails = remainingDays <= 0 
				? "Mobile App channel: Your licence has expired"
				: "Mobile App channel: Your licence will expire in " + remainingDays + " day(s) time";
		
		String sql = "INSERT INTO ALERT ( ALERT_ID, ALERT_REF, ORIGINATOR_USER_ROLE_ID, CUST_ID, SUBJECT,\r\n" + 
				"        ALERT_PRIORITY_CD, ALERT_CAT_CD, ALERT_SUB_TY_CD, DETAILS, REC_ST,\r\n" + 
				"        VERSION_NO, ROW_TS, USER_ID, CREATE_DT, CREATED_BY, SYS_CREATE_TS) \r\n" + 
				"   VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		// create parameter list in the order needed by the query
		List<ValueDatatypePair> inParam = new ArrayList<ValueDatatypePair>();
		inParam.add(new ValueDatatypePair(alertId, Types.NUMERIC));
		inParam.add(new ValueDatatypePair(alertRefNo, Types.VARCHAR));
		inParam.add(new ValueDatatypePair(-99, Types.NUMERIC));
		inParam.add(new ValueDatatypePair(custId, Types.VARCHAR));
		inParam.add(new ValueDatatypePair(alertSubject, Types.VARCHAR));
		inParam.add(new ValueDatatypePair("1", Types.VARCHAR));
		inParam.add(new ValueDatatypePair("SN", Types.VARCHAR));
		inParam.add(new ValueDatatypePair(alertSubTypeCode, Types.VARCHAR));
		inParam.add(new ValueDatatypePair(alertDetails, Types.VARCHAR));
		inParam.add(new ValueDatatypePair("A", Types.VARCHAR));
		inParam.add(new ValueDatatypePair(1, Types.NUMERIC));
		inParam.add(new ValueDatatypePair(new Date(), Types.DATE));
		inParam.add(new ValueDatatypePair("NEPTUNE", Types.VARCHAR));
		inParam.add(new ValueDatatypePair(new Date(), Types.DATE));
		inParam.add(new ValueDatatypePair("SYSTEM", Types.VARCHAR));
		inParam.add(new ValueDatatypePair(new Date(), Types.DATE));
		
		int numOfRow = executeDML(sql, inParam);
		
		if(numOfRow > 0)
			sendNotificationToWorkflow(userRoleId, alertId);
		
		return numOfRow;
	}
	
	private int sendNotificationToWorkflow(long userRoleId, long alertId) {
		
		long userRoleAlertId = nextRecordIdFromTable("USER_ROLE_ALERT");
				
		String sql = "INSERT INTO USER_ROLE_ALERT (USER_ROLE_ALERT_ID, ALERT_ID, USER_ROLE_ID, REC_ST, VERSION_NO,ROW_TS, USER_ID, CREATE_DT, CREATED_BY, SYS_CREATE_TS)\r\n" + 
				"    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		// create parameter list in the order needed by the query
		List<ValueDatatypePair> inParam = new ArrayList<ValueDatatypePair>();
		inParam.add(new ValueDatatypePair(userRoleAlertId, Types.NUMERIC));
		inParam.add(new ValueDatatypePair(alertId, Types.NUMERIC));
		inParam.add(new ValueDatatypePair(userRoleId, Types.NUMERIC));
		inParam.add(new ValueDatatypePair("U", Types.VARCHAR));
		inParam.add(new ValueDatatypePair(1, Types.NUMERIC));
		inParam.add(new ValueDatatypePair(new Date(), Types.DATE));
		inParam.add(new ValueDatatypePair("NEPTUNE", Types.VARCHAR));
		inParam.add(new ValueDatatypePair(new Date(), Types.DATE));
		inParam.add(new ValueDatatypePair("SYSTEM", Types.VARCHAR));
		inParam.add(new ValueDatatypePair(new Date(), Types.DATE));
		
		int numOfRow = executeDML(sql, inParam);
		
		return numOfRow;
	}
	
	private long nextRecordIdFromTable(String tableName) {

		String sql = "SELECT   ENTITY_ID, ENTITY_NM, NEXT_NO FROM ENTITY WHERE ENTITY_NM = ?";

		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(tableName, Types.VARCHAR));

		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(sql, params);

		long nextId = 0;

		// Null is returned when an exception is thrown.
		if (records == null) {
			return nextId;
		}

		// Loop through each row returned
		for (int rowIndex = 1; rowIndex <= records.size(); rowIndex++) {
			// collect the columns and access its value by
			// first the column alias or column names as returned by the query

			nextId = Long.valueOf(records.get(rowIndex).get("NEXT_NO".toUpperCase()));

			updateEntityWithNextRecordId(tableName, ++nextId);
		}

		return nextId;
	}
	
	private int updateEntityWithNextRecordId(String tableName, long newRecordId) {
		
		String sql = "UPDATE ENTITY SET NEXT_NO = " + newRecordId + 
				"\r\n WHERE ENTITY_NM = '" + tableName + "'";
		
		int numOfRow = executeDML(sql);
		
		return numOfRow;
	}
		
	
	public int updatePassword(String accountNumber, String newPassword, String deviceUIID) {
		
		String sql = "UPDATE ALT_MAPP_DEVICE SET PASSWORD = ? WHERE ACCT_NUM = ? AND DEVICE_UIID = ? ";
		
		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(newPassword, Types.VARCHAR));
		params.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));
		params.add(new ValueDatatypePair(deviceUIID, Types.VARCHAR));
				
		int numOfRow = executeDML(sql, params);
		
		return numOfRow;
	}
	
	public int updatePin(String accountNumber, String newPin, String deviceUIID) {
		
		String sql = "UPDATE ALT_MAPP_DEVICE SET ACCESSPIN = ? WHERE ACCT_NUM = ? AND DEVICE_UIID = ? ";
		
		// input parameters in the order needed in the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair(newPin, Types.VARCHAR));
		params.add(new ValueDatatypePair(accountNumber, Types.VARCHAR));
		params.add(new ValueDatatypePair(deviceUIID, Types.VARCHAR));
				
		int numOfRow = executeDML(sql, params);
		
		return numOfRow;
	}
	
	
}
