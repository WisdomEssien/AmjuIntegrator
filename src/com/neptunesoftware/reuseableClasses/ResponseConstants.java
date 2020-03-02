package com.neptunesoftware.reuseableClasses;

public class ResponseConstants {
	/*
	 * Response Codes and Messages
	 */

	// success
	public static final String SUCCEESS_CODE = "00";
	public static final String SUCCEESS_MESSAGE = "Successful";

	//insufficient funds
	public static final String INSUFFICIENT_CODE = "01";
	public static final String INSUFFICIENT_MESSAGE = "Insufficient Funds";
	
	// web service unavailable
	public static final String WEBSERVICE_UNAVAILABLE_CODE = "02";
	public static final String WEBSERVICE_UNAVAILABLE_MESSAGE = "Web Service unreachable";

	// web service returned response code other than success
	public static final String WEBSERVICE_FAILED_RESPONSE_CODE = "03";

	// procedure error
	public static final String PROCEDURE_CODE = "04";
	public static final String PROCEDURE_MESSAGE = "Procedure failed with response ";

	// already exist
	public static final String ALREADY_EXIST_CODE = "05";
	public static final String ALREADY_EXIST_MESSAGE = "Record already exist ";

	// not found
	public static final String NOT_FOUND_CODE = "06";
	public static final String NOT_FOUND_MESSAGE = "Not Found";

	// query error
	public static final String QUERY_CODE = "07";
	public static final String QUERY_MESSAGE = "Check your query";

	// converting string to java object
	public static final String UNMARSHAL_CODE = "08";
	public static final String UNMARSHAL_MESSAGE = "Unmarshalling/Deserialize error: Could not convert String to class object."
													+ "\n Check xml/json String to object class created";

	// mandatory parameter has not been passed
	public static final String MANDATORY_CODE = "09";
	public static final String MANDATORY_MESSAGE = "A mandatory parameter has not been passed: ";

	//file error
	public static final String FILE_ERROR_CODE = "10";
	public static final String FILE_ERROR_MESSAGE = "Cannot read from file. Check whether it exist: ";

	// exception
	public static final String EXCEPTION_CODE = "11";
	public static final String EXCEPTION_MESSAGE = "Exception was thrown ";
	
	// wrong login credentials
	public static final String LOGIN_CODE = "12";
	public static final String LOGIN_MESSAGE = "Invalid Username Or Password or Pin";
	
	// expired app version
	public static final String VERSION_CODE = "13";
	public static final String VERSION_MESSAGE = "Version does not match";
	
	// expired app version
	public static final String LICENCE_CODE = "14";
	public static final String LICENCE_MESSAGE = "License has expired";
	
	// expired app version
	public static final String DEVICE_CODE = "15";
	public static final String DEVICE_MESSAGE = "Device UIID does not match";

	// 00 - Success
	// 01 - insufficient balance
	// 02 - web service is unreachable
	// 03 - web service returned a failure response code
	// 04 - procedure failure
	// 05 - record already exist
	// 06 - Not found
	// 07 - wrong query
	// 08 - web service response received cannot be converted to a java class object
	// 09 - mandatory parameter has not been passed
	// 10 - file doesn't exist or a wrong file name specified'
	// 11 - exception
	// 12 - wrong login credentials
	// 13 - a different app version from the current on
	// 14 - expired license
	// 15 - a different device from the original is being used
	
	
	
	// zenith web service returned response calling authentication endpoint
	public static final String ZENITH_AUTHENTICATION_MESSAGE = "00-Success";

	public static final String CREDIT_SCORE_RATING_SUCCESS = "GOOD";
	public static final String CREDIT_SCORE_RATING_FAILURE = "BAD";

	public static final String SERVICE_TYPE_CREDIT_BUREAU = "CREDIT_BUREAU_SCORE";
	
	
	
	public static final int DEFAULT_RETURNED_VALUE = -1;
	
	/*
	 * Database Types
	 */

	// oracle
	public static final String ORACLE_DATABASE = "ORACLE";
	public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	public static final String ORACLE_CONNECTION_URL_PREFIX = "jdbc:oracle:thin:@";

	// sybase
	public static final String SYBASE_DATABASE = "SYBASE";
	public static final String SYBASE_DRIVER = "com.sybase.jdbc4.jdbc.SybDriver";
	public static final String SYBASE_CONNECTION_URL_PREFIX = "jdbc:sybase:Tds:";

}
