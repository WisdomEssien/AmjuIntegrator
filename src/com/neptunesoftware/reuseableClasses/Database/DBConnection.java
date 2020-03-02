package com.neptunesoftware.reuseableClasses.Database;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.company.application.airtimeRecharge.data.AirtimeRechargeRequest;
import com.company.application.billsPayment.data.BillPaymentRequest;
import com.company.application.fundTransfer.data.ExternalFTRequest;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.CypherCrypt;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.QuicktellerConstants;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;

public class DBConnection {

	private static String driver = "";
	private static String username = "";
	private static String password = "";
	private static String connectionURL = "";
	private static String databaseType = "";
	private static String ipAddress = "";
	private static String portNumber = "";
	private static String serviceName = "";

	// //private String connectionURL = "jdbc:oracle:thin:@localhost:1521:orcl";
	
	public DBConnection() {
		
		// used to know whether DatabaseInfo.xml has a default DB set
		boolean hasDefaultDB = false;
		
		Database database = readConfig();
		
		if (database.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE)) {
			for (DatabaseProperty dbProperty : database.getDatabaseProps()) {
				if (dbProperty.getDefaultDB().trim().equalsIgnoreCase("true")
					|| dbProperty.getDefaultDB().trim().equalsIgnoreCase("yes")) {

					hasDefaultDB = true; // yes there is a default DB set
					
					DBConnection.driver = getDatabaseDriver(dbProperty.getType().toUpperCase());
					DBConnection.username = dbProperty.getUsername().trim();
					DBConnection.password = dbProperty.getPassword().trim();
					DBConnection.ipAddress = dbProperty.getIpAddress().trim();
					DBConnection.portNumber = dbProperty.getPortNumber().trim();
					DBConnection.serviceName = dbProperty.getServiceName().trim();
					DBConnection.connectionURL = getDatabaseConnectionUrl(dbProperty.getType().toUpperCase(), ipAddress, portNumber,
							serviceName);
					DBConnection.databaseType = "default [" + dbProperty.getAlias().trim().toUpperCase() + "]";
				}
			}
			
			// in the absence of a default DB, the first DB config is used
			if(!hasDefaultDB) {
				for (DatabaseProperty dbProperty : database.getDatabaseProps()) {					
					DBConnection.driver = getDatabaseDriver(dbProperty.getType().toUpperCase());
					DBConnection.username = dbProperty.getUsername().trim();
					DBConnection.password = dbProperty.getPassword().trim();
					DBConnection.ipAddress = dbProperty.getIpAddress().trim();
					DBConnection.portNumber = dbProperty.getPortNumber().trim();
					DBConnection.serviceName = dbProperty.getServiceName().trim();
					DBConnection.connectionURL = getDatabaseConnectionUrl(dbProperty.getType().toUpperCase(), ipAddress, portNumber,
							serviceName);
					DBConnection.databaseType = dbProperty.getType().toUpperCase();
					
					System.out.println("\nNo default database was set. First database config is used instead \n");
					break;
				}
			}
		}
		else {
			DBConnection.driver = "";
			DBConnection.username = "";
			DBConnection.password = "";
			DBConnection.ipAddress = "";
			DBConnection.portNumber = "";
			DBConnection.serviceName = "";
			DBConnection.connectionURL = "";
			DBConnection.databaseType = "";
		}
	}
	
	public DBConnection(final String databaseAliasOrUsername) {
		
		Database database = readConfig();
			
		if (database.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE)) {
			for (DatabaseProperty dbProperty : database.getDatabaseProps()) {
				if (dbProperty.getAlias().trim().toUpperCase().equals(databaseAliasOrUsername.toUpperCase())
					|| dbProperty.getUsername().trim().toUpperCase().equals(databaseAliasOrUsername.toUpperCase())) {

					DBConnection.driver = getDatabaseDriver(dbProperty.getType().toUpperCase());
					DBConnection.username = dbProperty.getUsername().trim();
					DBConnection.password = dbProperty.getPassword().trim();
					DBConnection.ipAddress = dbProperty.getIpAddress().trim();
					DBConnection.portNumber = dbProperty.getPortNumber().trim();
					DBConnection.serviceName = dbProperty.getServiceName().trim();
					DBConnection.connectionURL = getDatabaseConnectionUrl(dbProperty.getType().toUpperCase(), ipAddress, portNumber,
							serviceName);
				}
			}

			DBConnection.databaseType = databaseAliasOrUsername;
		}
		else {
			DBConnection.driver = "";
			DBConnection.username = "";
			DBConnection.password = "";
			DBConnection.ipAddress = "";
			DBConnection.portNumber = "";
			DBConnection.serviceName = "";
			DBConnection.connectionURL = "";
			DBConnection.databaseType = databaseAliasOrUsername;
		}
	}
	
	public DBConnection(final String driver, final String connectionURL, final String username, final String password, final String databaseType) {
		DBConnection.driver = driver;
		DBConnection.username = username;
		DBConnection.password = password;
		DBConnection.connectionURL = connectionURL;
		DBConnection.databaseType = databaseType;
	}

	
	
 	protected static Connection databaseConnection() {
		Connection connection = null;

		try {
			Class.forName(driver);

			Properties props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", password);
			props.setProperty("charset", "iso_1");
			
			connection = DriverManager.getConnection(connectionURL, props);
			//connection = DriverManager.getConnection(connectionURL, username, password);
			
			System.out.println("connection to " + databaseType.toUpperCase() + " database established");

		} catch (ClassNotFoundException e) {
			System.out.println("** Suggested fix **");
			System.out.println("1. Ensure the appropriate jar needed for your database connection has been added to the project");
			System.out.println("2. Ensure the TYPE specified in DatabaseInfo.xml file for your database, " + databaseType.toUpperCase() + ", is either \"Oracle\" or \"Sybase\" ");
			System.out.println("\nconnection to " + databaseType.toUpperCase() + " database failed");
			
		} catch (SQLException e) {
			System.out.println("Please Verify your connection parameters and that your " + databaseType.toUpperCase() + " database is started");
			System.out.println("driver: " + driver + "\nusername: " + username + "\npassword: " + password);
			System.out.println("ipAddress: " + ipAddress + "\nportNumber: " + portNumber+ "\nserviceName: " + serviceName);
			System.out.println("\nconnection to " + databaseType.toUpperCase() + " database failed");
		
		} catch (Exception e) {
			System.out.println("This error pass me!");
			System.out.println("*** Stack Trace *** \n" + e);
			System.out.println("\nconnection to " + databaseType.toUpperCase() + " database failed");
		}
		
		return connection;
	}

	
	public static void main(String [] args) {
		//System.out.println(new DBConnection("school").selectProcessingDate());

		//System.out.println(new DBConnection("").databaseConnection());
		
		System.out.println("Result: " + new DBConnection().saveSupposedReversal(
				"2002080634", 
				"004567382", 
				Double.valueOf("100"), 
				"01", 
				"Webservice Unavailable", 
				"ExternalTransfer", 
				Double.valueOf("50"), 
				Double.valueOf("2.5")));
	
	}
	
	protected String selectProcessingDate() {
		// default date
		String processingDate = "01/01/1900";
		
		// select query
		String query = "SELECT to_char(to_date(DISPLAY_VALUE, 'DD/MM/YYYY'), 'YYYYMMDD') processingDate FROM CTRL_PARAMETER WHERE PARAM_CD = ?";
		
		// input parameters to the query
		List<ValueDatatypePair> params = new ArrayList<ValueDatatypePair>();
		params.add(new ValueDatatypePair("S65", Types.VARCHAR));
		
		// collect the result
		HashMap<Integer, HashMap<String, String>> records = executeSelect(query, params);
		
		// Null is returned when an exception is thrown.
		// Map is empty when no record is returned from query
		if(records == null) {
			return processingDate;
		}
				
		// Loop through each row returned
		for(Map.Entry<Integer, HashMap<String, String>> rowEntrySet : records.entrySet()) {
			// collect the column Map and access its value by column alias/ name as used in the query
			processingDate = rowEntrySet.getValue().get("processingDate".toUpperCase()); // processingDate
		}
				
		return processingDate;
		
	}
	
	public boolean tableExist(String tableName) {
	    boolean tExists = false;
	    
	    tableName = tableName.toUpperCase();
	    
	    Connection dbConnection = null;
		dbConnection = databaseConnection();
		
	    try {
	    	ResultSet rs = dbConnection.getMetaData().getTables(null, null, tableName, null);
	        while (rs.next()) { 
	            String tName = rs.getString("TABLE_NAME");
	            if (tName != null && tName.equals(tableName)) {
	                tExists = true;
	                break;
	            }
	        }
	    } catch (SQLException e) {
	    	System.out.println("table check failed");
		}
	    return tExists;
	}
	
	public boolean procedureExist(String procedureName) {
	    boolean tExists = false;
	    
	    procedureName = procedureName.toUpperCase();
	    
	    Connection dbConnection = null;
		dbConnection = databaseConnection();
		
	    try {
	    	ResultSet rs = dbConnection.getMetaData().getProcedures(null, null, procedureName);
	        while (rs.next()) { 
	            String tName = rs.getString("PROCEDURE_NAME");
	            if (tName != null && tName.equals(procedureName)) {
	                tExists = true;
	                break;
	            }
	        }
	    } catch (SQLException e) {
	    	System.out.println("procedure check failed");
		}
	    return tExists;
	}

	public boolean isDatabaseObjectCreated(String query) {
		Connection dbConnection = null;
		dbConnection = databaseConnection();
		
		PreparedStatement pst = null;
		
		try {

			pst = dbConnection.prepareStatement(query);
			int result = pst.executeUpdate();

			if(!(result == 0)) {
				System.out.println("failed to create object");
				return false;
			}
			
			System.out.println("object created!");			
			return true;
		}

		catch (Exception e) {
			System.out.println("failed to create object(check query)");
			return false;
		} finally {
			try {
				if (dbConnection != null) {
					dbConnection.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {}
		}
		
	}
	
	
	/* To execute Oracle Procedures  */
	
	protected LinkedHashMap<Integer, ValueDatatypePair> executeProcedure(final String procedureName){
		// execute procedures without parameters
		return executeProcedure(procedureName, null, null);
	}
	
	protected LinkedHashMap<Integer, ValueDatatypePair> executeProcedure(final String procedureName, final LinkedHashMap<Integer, ValueDatatypePair> inParam){
		// execute procedures with only IN parameters
		return executeProcedure(procedureName, inParam, null);
	}
	
	protected LinkedHashMap<Integer, ValueDatatypePair> executeProcedure(final String procedureName, final LinkedHashMap<Integer, ValueDatatypePair> inParam,
			LinkedHashMap<Integer, ValueDatatypePair> outParam){
		
		String storedProcedure= "{call " + procedureName + withParameters(inParam, outParam) + "}";

		Connection dbConnection = null;
		dbConnection = databaseConnection();

		CallableStatement callableStatement = null;

		try {
			callableStatement = dbConnection.prepareCall(storedProcedure);
			
			callableStatement = setInParamCallablestatement(callableStatement, inParam);

			callableStatement = registerOutParameter(callableStatement, outParam);
			
			callableStatement.executeUpdate();
			
			//System.out.println("\nExecuted procedure " +procedureName.toUpperCase()+" successfully\n");
						
			LinkedHashMap<Integer, ValueDatatypePair> getOutParam = getOutParamCallablestatement(callableStatement, outParam);
			
			LinkedHashMap<Integer, ValueDatatypePair> resultParam = new LinkedHashMap<Integer, ValueDatatypePair>();
			
			if(getOutParam != null)
				resultParam = getOutParam;
			
			//indicates success
			resultParam.put(0, new ValueDatatypePair(ResponseConstants.SUCCEESS_CODE));
			
			return resultParam;
			
		} catch (Exception e) {
			
			System.out.println("Exception: Error while trying to execute procedure " + procedureName.toUpperCase());
			
			//indicates failure
			outParam.put(0, new ValueDatatypePair(ResponseConstants.PROCEDURE_CODE));
			return outParam;
		}finally {
			try {
				if (dbConnection != null) {
					dbConnection.close();
				}
				if (callableStatement != null) {
					callableStatement.close();
				}
			} catch (SQLException e) {
			}
		}
	}

	private String withParameters(final LinkedHashMap<Integer, ValueDatatypePair> inParam, final LinkedHashMap<Integer, ValueDatatypePair> outParam) {
		
		
		
		int numOfParameters = calculateNumberOfParameters(inParam, outParam);
		String parameters = "(" + questionMarks(numOfParameters) + ")" ;
				
		return parameters.equals("()") ? "" : parameters;
	}
	
	private int calculateNumberOfParameters(final LinkedHashMap<Integer, ValueDatatypePair> inParam, final LinkedHashMap<Integer, ValueDatatypePair> outParam) {
		
		int numOfDuplicateParameters = 0;
		
		if (inParam != null && outParam != null) {
			for(Map.Entry<Integer, ValueDatatypePair> entry : inParam.entrySet()) {
				if(outParam.containsKey(entry.getKey()))
					numOfDuplicateParameters++;
			}
		}
		
		int numOfInparameters = inParam == null ? 0 : inParam.size();
		int numOfOutparameters = outParam == null ? 0 : outParam.size();
		
		int estimatedNumOfParameters = numOfInparameters + numOfOutparameters;
		
		int actualNumOfParameters = estimatedNumOfParameters - numOfDuplicateParameters;
		
		return actualNumOfParameters;
	}
	
	private static String questionMarks(int numOfParam) {
		if (numOfParam == 1)
			return "?";
		else if(numOfParam > 1)
			return "?," + questionMarks(numOfParam - 1);
		else
			return "";
	}
	
	private static CallableStatement setInParamCallablestatement(final CallableStatement callableStatement, final LinkedHashMap<Integer, ValueDatatypePair> param) {
		try {
			
			int paramIndex = 0, datatype = 0;
			String value = "";
			
			for (Map.Entry<Integer, ValueDatatypePair> entry : param.entrySet()) {
				
				paramIndex = entry.getKey();
				value = entry.getValue().getValue();
				datatype = entry.getValue().getType();

				switch (datatype) {
				case Types.VARCHAR:
					callableStatement.setString(paramIndex, value);
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					callableStatement.setBigDecimal(paramIndex, new BigDecimal(value));
					break;
				case Types.DOUBLE:
					callableStatement.setDouble(paramIndex, Double.valueOf(value));
					break;
				case Types.FLOAT:
					callableStatement.setFloat(paramIndex, Float.valueOf(value));
					break;
				case Types.INTEGER:
				case Types.BIGINT:
				case Types.SMALLINT:
					callableStatement.setInt(paramIndex, Integer.valueOf(value));
					break;
				case Types.DATE:
					callableStatement.setDate(paramIndex, getCurrentDate(value));
					break;
				case Types.NULL:
					callableStatement.setNull(paramIndex, Types.NULL);
					break;
//				case Types.TIMESTAMP:
//					preparedStatement.setTimestamp(paramIndex, Timestamp.valueOf(value));
//					break;
				default:
					System.out.println("\nDatatype wrongly or not specified when setting in params for prepared statement");
				}
			}
		} catch (Exception e) {
			System.out.println("preparedStatement/param is probably null in setting IN parameters");
		}
		return callableStatement;
	}
	
	private static CallableStatement registerOutParameter(final CallableStatement callableStatement, final LinkedHashMap<Integer, ValueDatatypePair> param) {
		try {
			
			int paramIndex = 0, datatype = 0;
			//String value = "";
			
			for (Map.Entry<Integer, ValueDatatypePair> entry : param.entrySet()) {
				
				//value = entry.getValue().getValue();
				paramIndex = entry.getKey();
				datatype = entry.getValue().getType();

				switch (datatype) {
				case Types.VARCHAR:
					callableStatement.registerOutParameter(paramIndex, java.sql.Types.VARCHAR);
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					callableStatement.registerOutParameter(paramIndex, java.sql.Types.NUMERIC);
					break;
				case Types.DOUBLE:
					callableStatement.registerOutParameter(paramIndex, java.sql.Types.DOUBLE);
					break;
				case Types.FLOAT:
					callableStatement.registerOutParameter(paramIndex, java.sql.Types.FLOAT);
					break;
				case Types.INTEGER:
				case Types.BIGINT:
				case Types.SMALLINT:
					callableStatement.registerOutParameter(paramIndex, java.sql.Types.INTEGER);
					break;
				case Types.DATE:
					callableStatement.registerOutParameter(paramIndex, java.sql.Types.DATE);
					break;
//				case Types.TIMESTAMP:
//					callableStatement.registerOutParameter(paramIndex, java.sql.Types.TIMESTAMP);
//					break;
				default:
					System.out.println("\nDatatype wrongly or not specified for out parameter");
				}
			}
		} catch (Exception e) {
			System.out.println("callablestatement/param is probably null in registering out parameters");
		}
		return callableStatement;
	}	
	
	private static LinkedHashMap<Integer, ValueDatatypePair> getOutParamCallablestatement(final CallableStatement callableStatement, final LinkedHashMap<Integer, ValueDatatypePair> param) {
		try {
			
			int paramIndex = 0, datatype = 0;
			//String value = "";
			
			for (Map.Entry<Integer, ValueDatatypePair> entry : param.entrySet()) {
				
				//value = entry.getValue().getValue();
				datatype = entry.getValue().getType();
				paramIndex = entry.getKey();

				switch (datatype) {
				case Types.VARCHAR:
					entry.getValue().setValue(callableStatement.getString(paramIndex) + "");
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					entry.getValue().setValue(callableStatement.getBigDecimal(paramIndex) + "");
					break;
				case Types.DOUBLE:
					entry.getValue().setValue(callableStatement.getDouble(paramIndex) + "");
					break;
				case Types.FLOAT:
					entry.getValue().setValue(callableStatement.getFloat(paramIndex) + "");
					break;
				case Types.INTEGER:
				case Types.BIGINT:
				case Types.SMALLINT:
					entry.getValue().setValue(callableStatement.getInt(paramIndex) + "");
					break;
				case Types.DATE:
					entry.getValue().setValue(callableStatement.getDate(paramIndex) + "");
					break;
//				case Types.TIMESTAMP:
//					entry.getValue().setValue(callableStatement.getTimestamp(paramIndex) + "");
//					break;
				default:
					System.out.println("\nDatatype wrongly or not specified for getting out parameter");
				}
			}
		} catch (Exception e) {
			System.out.println("callablestatement is probably null in getting out parameters");
		}
		
		return param;
	}
	
	
	/* To execute SELECT statement  */
	
	protected HashMap<Integer, HashMap<String, String>> executeSelect(final String query){
		return executeSelect(query, null);
	}
	
	protected HashMap<Integer, HashMap<String, String>> executeSelect(final String query, final List<ValueDatatypePair> inputParameter) {
		
		Connection dbConnection = null;
		dbConnection = databaseConnection();
			
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			pst = dbConnection.prepareStatement(query);
			
			pst = setInParamPreparedStatement(pst, inputParameter);
			
			rs = pst.executeQuery();
									
			System.out.println("Select successful");
			
			return convertResultSetToHashMap(rs);
		} catch (Exception e) {
			System.out.println("Select failed");
			return null;
		} finally {
			try {
				if (dbConnection != null) {
					dbConnection.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
			}
		}
	}
	
	private HashMap<Integer, HashMap<String, String>> convertResultSetToHashMap(ResultSet rs) {
		
		HashMap<Integer, HashMap<String, String>> row = new HashMap<Integer, HashMap<String, String>>();
				
		try {
			
			ResultSetMetaData rsmd = rs.getMetaData();
			
			// get the number of columns returned from select query
			int numberOfColumns = rsmd.getColumnCount();
						
			int rowIndex = 0;
			String key = "", value = "";
			
			// loop through each row of records
			while(rs.next()) {

				HashMap<String, String> column = new HashMap<String, String>();
				
				// loop through each column returned
				for(int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex++) {
					
					key = rsmd.getColumnLabel(columnIndex);
					value = rs.getString(columnIndex) == null ? "" : rs.getString(columnIndex);
					
					// add columns accessed by column name
					column.put(key.toUpperCase(), value);
				}
				
				// add rows accessed by row index
				row.put(++rowIndex, column);
			}
			
		} catch (Exception e) {
			System.out.println("Exception when converting ResultSet to HashMap");
		}
		
		return row;
	}
	
	
	
	/* To execute INSERT, DELETE, UPDATE statements  */
	
	protected int executeDML(final String query) {
		
		List<ValueDatatypePair> inputParameter = new ArrayList<ValueDatatypePair>();
		
		return executeDML(query, inputParameter);	
	}
		
	protected int executeDML(final String query, final List<ValueDatatypePair> inputParameter) {
		int result = executeSQL(query, inputParameter);
		
		String message = "";
		if (result > 0)
			message = "successful";
		else
			message = "failed";
		
		if(query.toLowerCase().trim().startsWith("insert"))
			System.out.println("Insert " + message);
		
		else if(query.toLowerCase().trim().startsWith("update"))
			System.out.println("Update " + message);
		
		else if(query.toLowerCase().trim().startsWith("delete"))
			System.out.println("Delete " + message);
		
		else
			System.out.println("your query is likely not correct!");
				
		return result;	
	}
	
	private int executeSQL(final String query, final List<ValueDatatypePair> inputParameter) {
		Connection dbConnection = null;
		dbConnection = databaseConnection();

		PreparedStatement pst = null;

		int result = -1;
		
		try {
			pst = dbConnection.prepareStatement(query);
			
			pst = setInParamPreparedStatement(pst, inputParameter);
			
			result = pst.executeUpdate();
			
		} catch (Exception e) {
			System.out.println("exception: " + e.getMessage());
		} finally {
			try {
				if (dbConnection != null) {
					dbConnection.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
			}
		}
		
		return result;
	}
	
	private static PreparedStatement setInParamPreparedStatement(final PreparedStatement preparedStatement, final List<ValueDatatypePair> param) {
		try {
			
			int paramIndex = 0;
			
			for (ValueDatatypePair entry : param) {
				
				String value = entry.getValue();
				int datatype = entry.getType();

				switch (datatype) {
				case Types.VARCHAR:
					preparedStatement.setString(++paramIndex, value);
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					preparedStatement.setBigDecimal(++paramIndex, new BigDecimal(value));
					break;
				case Types.DOUBLE:
					preparedStatement.setDouble(++paramIndex, Double.valueOf(value));
					break;
				case Types.FLOAT:
					preparedStatement.setFloat(++paramIndex, Float.valueOf(value));
					break;
				case Types.INTEGER:
				case Types.BIGINT:
				case Types.SMALLINT:
					preparedStatement.setInt(++paramIndex, Integer.valueOf(value));
					break;
				case Types.DATE:
					preparedStatement.setDate(++paramIndex, getCurrentDate());
					break;
				case Types.NULL:
					preparedStatement.setNull(++paramIndex, Types.NULL);
					break;
//				case Types.TIMESTAMP:
//					preparedStatement.setTimestamp(++paramIndex, Timestamp.valueOf(value));
//					break;
				default:
					System.out.println("\nDatatype wrongly or not specified when setting in params for prepared statement");
				}
			}
		} catch (Exception e) {
			System.out.println("preparedStatement is probably null");
		}
		return preparedStatement;
	}


	
	
	
	
	
	
	//*** START very specific methods.
	// not really reusable except you are implementing quickteller
	
	public boolean isTransactionSuccessful(final AirtimeRechargeRequest airtimeRchgeXfer, final boolean isReversal, final String responseCode, final String transactionRef) {

		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();
		String chargeCodeBillsPayment = rubikonCredential.getChargeCodeBillsPayment();
		String channelCode = rubikonCredential.getChannelCode();
		String currencyCode = rubikonCredential.getCurrencyCode();
		String billsPaymentCredit = rubikonCredential.getBillsPaymentCredit();
		String billsPaymentDebit = rubikonCredential.getBillsPaymentDebit();
		
		
		System.out.println("**Start callProcedure");
		
		String narration = "Airtime recharge for mobile no " + airtimeRchgeXfer.getCustomerId();
		
		double transactionAmount = CommonMethods.koboToNaira(Integer.parseInt(airtimeRchgeXfer.getTransactionAmount().trim()));
		double chargeAmount = CommonMethods.koboToNaira(Double.parseDouble(airtimeRchgeXfer.getChargeAmount().trim()));
		double taxAmount = CommonMethods.koboToNaira(Double.parseDouble(airtimeRchgeXfer.getTaxAmount().trim()));
		String serviceCode = billsPaymentDebit;
		String transactionType = "DR";
		String reveralFlag = "N";
		
		if(isReversal) {
			narration = "(Reversal) " + narration;
			
			double reversalCharges = Double.sum(chargeAmount, taxAmount);
			transactionAmount =		Double.sum(transactionAmount, reversalCharges);
			chargeAmount = 0;
			taxAmount = 0;
			serviceCode = billsPaymentCredit;
			transactionType = "CR";
			//reveralFlag = "Y";
		}
		
		
		DBRequest dbRequest = new DBRequest(airtimeRchgeXfer.getFromAccountNumber(), transactionAmount+"", "0",
				airtimeRchgeXfer.getMobileNumber(), narration, chargeAmount+"", taxAmount+"", airtimeRchgeXfer.getInitiatingApp());
		
		
		String newTransactionRef = transactionRef.isEmpty() 
						? new QuicktellerConstants().getTransferCodePrefix() + "|" + new Date() + "|" + new Date().getTime()
						: transactionRef;
		
		String dbResponse = callProcedure(dbRequest, "", serviceCode, newTransactionRef, reveralFlag, chargeCodeBillsPayment);
		
		String newResponseCode = responseCode.isEmpty() ? dbResponse : responseCode;
		
		// save to the db
		try {
			saveRecord(dbRequest, newResponseCode, newTransactionRef, "MobileRecharge", transactionType,
					channelCode, currencyCode);
		} catch (Exception e) {
			System.out.println("Failed to save record");
		}
		
		// return false if deduction was not possible
		if (!dbResponse.equals("00")) {
			System.out.println("**End callProcedure");
			return false;
		}
		
		System.out.println("**End callProcedure");
		return true;
		
	}
	
	public boolean isTransactionSuccessful(final BillPaymentRequest billPayment, final boolean isReversal, final String responseCode, final String transactionRef) {

		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();
		String chargeCodeBillsPayment = rubikonCredential.getChargeCodeBillsPayment();
		String channelCode = rubikonCredential.getChannelCode();
		String currencyCode = rubikonCredential.getCurrencyCode();
		String billsPaymentCredit = rubikonCredential.getBillsPaymentCredit();
		String billsPaymentDebit = rubikonCredential.getBillsPaymentDebit();
		
		
		System.out.println("**Start callProcedure");
		
		String narration = "Bills payment #" + billPayment.getCustomerId(); 
		narration = narration + "\nSub Desc: " + billPayment.getTransactionDescription();
		
		double transactionAmount = CommonMethods.koboToNaira(Integer.parseInt(billPayment.getTransactionAmount().trim()));
		double chargeAmount = CommonMethods.koboToNaira(Double.parseDouble(billPayment.getChargeAmount().trim()));
		double taxAmount = CommonMethods.koboToNaira(Double.parseDouble(billPayment.getTaxAmount().trim()));
		String serviceCode = billsPaymentDebit;
		String transactionType = "DR";
		String reveralFlag = "N";
		
		if(isReversal) {
			narration = "(Reversal) " + narration;
			
			double reversalCharges = Double.sum(chargeAmount, taxAmount);
			transactionAmount =		Double.sum(transactionAmount, reversalCharges);
			chargeAmount = 0;
			taxAmount = 0;
			serviceCode = billsPaymentCredit;
			transactionType = "CR";
			//reveralFlag = "Y";
		}
		
		
		DBRequest dbRequest = new DBRequest(billPayment.getFromAccountNumber(), transactionAmount+"", "0",
				billPayment.getCustomerId(), narration, chargeAmount+"", taxAmount+"", billPayment.getInitiatingApp());
		
		
		String newTransactionRef = transactionRef.isEmpty() 
						? new QuicktellerConstants().getTransferCodePrefix() + "|" + new Date() + "|" + new Date().getTime()
						: transactionRef;
		
		String dbResponse = callProcedure(dbRequest, "", serviceCode, transactionRef, reveralFlag, chargeCodeBillsPayment);
		
		String newResponseCode = responseCode.isEmpty() ? dbResponse : responseCode;
		
		// save to the db
		try {
			saveRecord(dbRequest, newResponseCode, newTransactionRef, "BillsPayment", transactionType,
					channelCode, currencyCode);
		} catch (Exception e) {
			System.out.println("Failed to save record");
		}
		
		// return false if deduction was not possible
		if (!dbResponse.equals("00")) {
			System.out.println("**End callProcedure");
			return false;
		}
		
		System.out.println("**End callProcedure");
		return true;
		
	}
		
	public boolean isTransactionSuccessful(final String senderName, final ExternalFTRequest externalTransfer, final boolean isReversal, final String responseCode, final String transactionRef) {

		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();	
		String chargeCode = rubikonCredential.getChargeCode();
		String channelCode = rubikonCredential.getChannelCode();
		String currencyCode = rubikonCredential.getCurrencyCode();
		String fundXferCredit = rubikonCredential.getFundTransferCredit();
		String fundXferDebit = rubikonCredential.getFundTransferDebit();
		
		
		System.out.println("**Start callProcedure");
		
		String narration = "External Transfer from " + senderName + ", #acctno " + externalTransfer.getFromAccountNumber() + 
				" to #acctno " + externalTransfer.getBeneficiaryAccountNumber(); 
		narration = narration + "\nSub Desc: " + externalTransfer.getTransactionDescription();	
		
		double transactionAmount = CommonMethods.koboToNaira(Integer.parseInt(externalTransfer.getTransactionAmount().trim()));
		double chargeAmount = CommonMethods.koboToNaira(Double.parseDouble(externalTransfer.getChargeAmount().trim()));
		double taxAmount = CommonMethods.koboToNaira(Double.parseDouble(externalTransfer.getTaxAmount().trim()));
		String serviceCode = fundXferDebit;
		String transactionType = "DR";
		String reversalFlag = "N";
		
		if(isReversal) {
			narration = "(Reversal) " + narration;
			
			double reversalCharges = Double.sum(chargeAmount, taxAmount);
			transactionAmount =		Double.sum(transactionAmount, reversalCharges);
			chargeAmount = 0;
			taxAmount = 0;
			serviceCode = fundXferCredit;
			transactionType = "CR";
		}
		
		
		DBRequest dbRequest = new DBRequest(externalTransfer.getFromAccountNumber(), transactionAmount+"", "0",
				externalTransfer.getBeneficiaryAccountNumber(), narration, chargeAmount+"", taxAmount+"",
				externalTransfer.getInitiatingApp());
		
		
		String newTransactionRef = transactionRef.isEmpty() 
						? new QuicktellerConstants().getTransferCodePrefix() + "|" + new Date() + "|" + new Date().getTime()
						: transactionRef;
		
		String dbResponse = callProcedure(dbRequest, senderName, serviceCode, newTransactionRef, reversalFlag, chargeCode);
		
		String newResponseCode = responseCode.isEmpty() ? dbResponse : responseCode;
		
		// save to the db
		try {
			saveRecord(dbRequest, newResponseCode, newTransactionRef, "ExternalFundTransfer", transactionType,
					channelCode, currencyCode);
		} catch (Exception e) {
			System.out.println("Failed to save record");
		}
		
		// return false if deduction was not possible
		if (!dbResponse.equals("00")) {
			System.out.println("**End callProcedure");
			return false;
		}
		
		System.out.println("**End callProcedure");
		return true;
		
	}
	
	public String callProcedure(final DBRequest dbRequest, final String senderName,
			String serviceCode, String transactionRef, String reversalFlag, String chargeCode) {
		
		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();		
		String channelCode = rubikonCredential.getChannelCode();
		String taxCode = rubikonCredential.getTaxCode();
		String currencyCode = rubikonCredential.getCurrencyCode();
		String appUsername = rubikonCredential.getApplicationUsername();
				
		
		String contraCurrencyCode = !dbRequest.getContraAmount().equals("0") ? currencyCode : "";
		transactionRef = transactionRef.isEmpty() 
				? "INT-TXN-|" + new Date() + "|" + new Date().getTime()
				: transactionRef;
				
		DBResponse dbResponse = new DBResponse();
		
		try {
			dbResponse = ProcedureXAPI_POSTING_SERVICE(
					channelCode, // PV_CHANNEL_CD
					"", // PV_CHANNEL_PWD
					"123456789", // PV_DEVICE_ID
					serviceCode, // PV_SERVICE_CD
					"", // PV_TRANS_TYPE_CD
					dbRequest.getSenderAcctNo(), // PV_ACCOUNT_NO,
					currencyCode, // PV_ACCOUNT_CURRENCY_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getAmount()))), // PV_ACCOUNT_AMOUNT,
					currencyCode, // PV_TRANS_CURRENCY_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getAmount()))), // PV_TRANS_AMOUNT,
					
					new Date(), // PV_VALUE_DATE,
					new Date(), // PV_TRANS_DATE,
					transactionRef, // PV_TRANS_REF,
					transactionRef, // PV_SUPPLEMENTARY_REF,
					dbRequest.getNarration(), // PV_NARRATIVE,
					dbRequest.getBeneficiaryAcctNo(), // PV_CONTRA_ACCOUNT_NO,
					contraCurrencyCode, // PV_CONTRA_CURRENCY_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getContraAmount()))), // PV_CONTRA_AMOUNT,
					new BigDecimal(0), // PV_EXCHANGE_RATE,
					chargeCode, // PV_CHARGE_CD,
					
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getChargeAmount()))), // PV_CHARGE_AMOUNT,
					taxCode, // PV_TAX_CD,
					new BigDecimal(Math.abs(Double.valueOf(dbRequest.getTaxAmount()))), // PV_TAX_AMOUNT,
					reversalFlag, // PV_REVERSAL_FLG,
					"", // PV_ORIGIN_BANK_CD,
					"", // PV_SERVICE_PROVIDER,
					"", // PV_SERVICE_PROVIDER_SVC,
					"", // PV_USER_TYPE,
					appUsername, // PV_USER_ID,
					"", // PV_USER_PWD,
					
				   "12:45:30", // PV_TRANS_TIME
				   "N", // PV_EXTERNAL_COMMIT
				   "Y", // PV_PREVENT_DUPLICATES
				   "123456789", //PV_REQUEST_PWD
				   "DB", // PV_LOG_REPOSITORY
				   0, // PV_TEST_CALL_LEVEL
				   3, // PV_TRACE_LEVEL
					"", // PV_ERROR_CODE,
					0, // PV_ERROR_SEVERITY,
					"" // PV_ERROR_MESSAGE,
					);
		} catch (Exception e) {
			return "";
		}

		String responseCode = dbResponse.PV_ERROR_SEVERITY == null || dbResponse.PV_ERROR_SEVERITY.equals("0") ? "00"
				: dbResponse.PV_ERROR_SEVERITY;
		
		System.out.println("callProdecure_XAPI_POSTING_SERVICE Response: " + responseCode);

		//return response
		return responseCode;
	}
	
	public DBResponse ProcedureXAPI_POSTING_SERVICE(final String PV_CHANNEL_CD,
			final String PV_CHANNEL_PWD, final String PV_DEVICE_ID, final String PV_SERVICE_CD,
			final String PV_TRANS_TYPE_CD, final String PV_ACCOUNT_NO,
			final String PV_ACCOUNT_CURRENCY_CD, final BigDecimal PV_ACCOUNT_AMOUNT,
			final String PV_TRANS_CURRENCY_CD, final BigDecimal PV_TRANS_AMOUNT,
			final Date PV_VALUE_DATE, final Date PV_TRANS_DATE, final String PV_TRANS_REF,
			final String PV_SUPPLEMENTARY_REF, final String PV_NARRATIVE,
			final String PV_CONTRA_ACCOUNT_NO, final String PV_CONTRA_CURRENCY_CD,
			final BigDecimal PV_CONTRA_AMOUNT, final BigDecimal PV_EXCHANGE_RATE,
			final String PV_CHARGE_CD, final BigDecimal PV_CHARGE_AMOUNT,
			final String PV_TAX_CD, final BigDecimal PV_TAX_AMOUNT,
			final String PV_REVERSAL_FLG, final String PV_ORIGIN_BANK_CD,
			final String PV_SERVICE_PROVIDER, final String PV_SERVICE_PROVIDER_SVC,
			final String PV_USER_TYPE, final String PV_USER_ID, final String PV_USER_PWD,
			final String PV_TRANS_TIME, final String PV_EXTERNAL_COMMIT, final String PV_PREVENT_DUPLICATES, 
			final String PV_REQUEST_PWD, final String PV_LOG_REPOSITORY, final int PV_TEST_CALL_LEVEL,
			final int PV_TRACE_LEVEL, final String PV_ERROR_CODE, final int PV_ERROR_SEVERITY,
			final String PV_ERROR_MESSAGE
			 
			) {
		
		
		// IN parameter values in the order received by the procedure
		LinkedHashMap<Integer, ValueDatatypePair> inParam = new LinkedHashMap<Integer, ValueDatatypePair>();
		inParam.put(1, new ValueDatatypePair(PV_CHANNEL_CD, Types.VARCHAR));			// PV_CHANNEL_CD
		inParam.put(2, new ValueDatatypePair(PV_CHANNEL_PWD, Types.VARCHAR));			// PV_CHANNEL_PWD
		inParam.put(3, new ValueDatatypePair(PV_DEVICE_ID, Types.VARCHAR));				// PV_DEVICE_ID
		inParam.put(4, new ValueDatatypePair(PV_SERVICE_CD, Types.VARCHAR));			// PV_SERVICE_CD
		inParam.put(5, new ValueDatatypePair(PV_TRANS_TYPE_CD, Types.VARCHAR));			// PV_TRANS_TYPE_CD
		inParam.put(6, new ValueDatatypePair(PV_ACCOUNT_NO, Types.VARCHAR));			// PV_ACCOUNT_NO
		inParam.put(7, new ValueDatatypePair(PV_ACCOUNT_CURRENCY_CD, Types.VARCHAR));	// PV_ACCOUNT_CURRENCY_CD
		inParam.put(8, new ValueDatatypePair(PV_ACCOUNT_AMOUNT, Types.NUMERIC));		// PV_ACCOUNT_AMOUNT
		inParam.put(9, new ValueDatatypePair(PV_TRANS_CURRENCY_CD, Types.VARCHAR));		// PV_TRANS_CURRENCY_CD
		inParam.put(10, new ValueDatatypePair(PV_TRANS_AMOUNT, Types.NUMERIC));			// PV_TRANS_AMOUNT
		
		inParam.put(11, new ValueDatatypePair(getCurrentDate(), Types.DATE));			// PV_VALUE_DATE
		inParam.put(12, new ValueDatatypePair(getCurrentDate(), Types.DATE));			// PV_TRANS_DATE
		inParam.put(13, new ValueDatatypePair(PV_TRANS_REF, Types.VARCHAR));			// PV_TRANS_REF
		inParam.put(14, new ValueDatatypePair(PV_SUPPLEMENTARY_REF, Types.VARCHAR));	// PV_SUPPLEMENTARY_REF
		inParam.put(15, new ValueDatatypePair(PV_NARRATIVE, Types.VARCHAR));			// PV_NARRATIVE
		inParam.put(16, new ValueDatatypePair(PV_CONTRA_ACCOUNT_NO, Types.VARCHAR));	// PV_CONTRA_ACCOUNT_NO
		inParam.put(17, new ValueDatatypePair(PV_CONTRA_CURRENCY_CD, Types.VARCHAR));	// PV_CONTRA_CURRENCY_CD
		inParam.put(18, new ValueDatatypePair(PV_CONTRA_AMOUNT, Types.NUMERIC));		// PV_CONTRA_AMOUNT
		inParam.put(19, new ValueDatatypePair(PV_EXCHANGE_RATE, Types.NUMERIC));		// PV_EXCHANGE_RATE
		inParam.put(20, new ValueDatatypePair(PV_CHARGE_CD, Types.VARCHAR));			// PV_CHARGE_CD
				
		inParam.put(21, new ValueDatatypePair(PV_CHARGE_AMOUNT, Types.NUMERIC));		// PV_CHARGE_AMOUNT
		inParam.put(22, new ValueDatatypePair(PV_TAX_CD, Types.VARCHAR));				// PV_TAX_CD
		inParam.put(23, new ValueDatatypePair(PV_TAX_AMOUNT, Types.NUMERIC));			// PV_TAX_AMOUNT
		inParam.put(24, new ValueDatatypePair(PV_REVERSAL_FLG, Types.VARCHAR));			// PV_REVERSAL_FLG
		inParam.put(25, new ValueDatatypePair(null, Types.NULL));						// PV_ORIGIN_BANK_CD
		inParam.put(26, new ValueDatatypePair(null, Types.NULL));						// PV_SERVICE_PROVIDER
		inParam.put(27, new ValueDatatypePair(null, Types.NULL));						// PV_SERVICE_PROVIDER_SVC
		inParam.put(28, new ValueDatatypePair(PV_USER_TYPE, Types.VARCHAR));			// PV_USER_TYPE
		inParam.put(29, new ValueDatatypePair(PV_USER_ID, Types.VARCHAR));				// PV_USER_ID
		inParam.put(30, new ValueDatatypePair(PV_USER_PWD, Types.VARCHAR));				// PV_USER_PWD
				
		inParam.put(31, new ValueDatatypePair(PV_TRANS_TIME, Types.VARCHAR));			// PV_TRANS_TIME
		inParam.put(32, new ValueDatatypePair(PV_EXTERNAL_COMMIT, Types.VARCHAR));		// PV_EXTERNAL_COMMIT
		inParam.put(33, new ValueDatatypePair(PV_PREVENT_DUPLICATES, Types.VARCHAR));	// PV_PREVENT_DUPLICATES
		inParam.put(34, new ValueDatatypePair(PV_REQUEST_PWD, Types.VARCHAR));			// PV_REQUEST_PWD
		inParam.put(35, new ValueDatatypePair(PV_LOG_REPOSITORY, Types.VARCHAR));		// PV_LOG_REPOSITORY
		inParam.put(36, new ValueDatatypePair(PV_TEST_CALL_LEVEL, Types.NUMERIC));		// PV_TEST_CALL_LEVEL
		inParam.put(37, new ValueDatatypePair(PV_TRACE_LEVEL, Types.NUMERIC));			// PV_TRACE_LEVEL
		inParam.put(38, new ValueDatatypePair(PV_ERROR_CODE, Types.VARCHAR));			// PV_ERROR_CODE
		inParam.put(39, new ValueDatatypePair(PV_ERROR_SEVERITY, Types.NUMERIC));		// PV_ERROR_SEVERITY
		inParam.put(40, new ValueDatatypePair(PV_ERROR_MESSAGE, Types.VARCHAR));		// PV_ERROR_MESSAGE
				
		// OUT parameter values in the order received by the procedure
		LinkedHashMap<Integer, ValueDatatypePair> outParam = new LinkedHashMap<Integer, ValueDatatypePair>();
		outParam.put(35, new ValueDatatypePair(null, Types.VARCHAR));	// PV_LOG_REPOSITORY
		outParam.put(36, new ValueDatatypePair(null, Types.NUMERIC));	// PV_TEST_CALL_LEVEL
		outParam.put(37, new ValueDatatypePair(null, Types.NUMERIC));	// PV_TRACE_LEVEL
		outParam.put(38, new ValueDatatypePair(null, Types.VARCHAR));	// PV_ERROR_CODE
		outParam.put(39, new ValueDatatypePair(null, Types.NUMERIC)); // PV_ERROR_SEVERITY
		outParam.put(40, new ValueDatatypePair(null, Types.VARCHAR)); // PV_ERROR_MESSAGE
		
		// Execute procedure
		LinkedHashMap<Integer, ValueDatatypePair> resultParam = executeProcedure("XAPI_POSTING_SERVICE_V2", inParam, outParam);
				
		DBResponse dbResponse = new DBResponse();
		
		if (resultParam.get(0).getValue().equals("00")) {
			dbResponse.PV_LOG_REPOSITORY =	resultParam.get(35).getValue();	//PV_LOG_REPOSITORY
			dbResponse.PV_TEST_CALL_LEVEL =	resultParam.get(36).getValue(); //PV_TEST_CALL_LEVEL
			dbResponse.PV_TRACE_LEVEL = resultParam.get(37).getValue();		//PV_TRACE_LEVEL
			dbResponse.PV_ERROR_CODE = resultParam.get(38).getValue();		//PV_ERROR_CODE
			dbResponse.PV_ERROR_SEVERITY = resultParam.get(39).getValue();	//PV_ERROR_SEVERITY
			dbResponse.PV_ERROR_MESSAGE = resultParam.get(40).getValue();	//PV_ERROR_MESSAGE
		}
		
		return dbResponse;
		
	}
	
	public int saveRecord(final DBRequest dbRequest, final String responseCd, final String paymentReference, final String trans_method_name,
			final String trans_type, final String channelCode, final String Curr) {
		
		String Isreversal = dbRequest.getNarration().startsWith("(Reversal)") ?  "True" : "False";
		
			String sql = "INSERT INTO  "
					+ "ALT_QUICKTELLER(TRAN_APPL,TRAN_REF,FROM_ACCT_NUM,TRAN_RECEIVER,TRAN_AMOUNT,TRAN_STATUS,TRAN_NETHOD,NARRATION,TRAN_PURPOSE,CHANNEL_NAME ,ISREVERSAL,TRAN_DATE ,TRAN_TYPE ,PAYMENT_CURR, CHARGE_AMOUNT,TAX_AMOUNT) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			// create the parameter values in the order needed by the insert statement
			List<ValueDatatypePair> param = new ArrayList<ValueDatatypePair>();
			param.add(new ValueDatatypePair(dbRequest.getInitiatingApp(), Types.VARCHAR));
			param.add(new ValueDatatypePair(paymentReference, Types.VARCHAR));
			param.add(new ValueDatatypePair(dbRequest.getSenderAcctNo(), Types.VARCHAR));
			param.add(new ValueDatatypePair(dbRequest.getBeneficiaryAcctNo(), Types.VARCHAR));
			param.add(new ValueDatatypePair(dbRequest.getAmount(), Types.DOUBLE));
			param.add(new ValueDatatypePair(responseCd, Types.VARCHAR));
			param.add(new ValueDatatypePair(trans_method_name, Types.VARCHAR));
			param.add(new ValueDatatypePair(dbRequest.getNarration(), Types.VARCHAR));
			param.add(new ValueDatatypePair("", Types.VARCHAR));
			param.add(new ValueDatatypePair(channelCode, Types.VARCHAR));
			param.add(new ValueDatatypePair(Isreversal, Types.VARCHAR));
			param.add(new ValueDatatypePair(getCurrentDate() + "", Types.DATE));
			param.add(new ValueDatatypePair(trans_type, Types.VARCHAR));
			param.add(new ValueDatatypePair(Curr, Types.VARCHAR));
			param.add(new ValueDatatypePair(dbRequest.getChargeAmount(), Types.VARCHAR));
			param.add(new ValueDatatypePair(dbRequest.getTaxAmount(), Types.VARCHAR));
			
			return executeDML(sql, param);

	}
	
	public int saveSupposedReversal(String senderAcctNo, String receiverAcctNo,
			double transactionAmount, String errorCode, String errorDesc, String transactionMethod, 
			double chargeAmount, double taxAmount){
		
		String transactionReference = new QuicktellerConstants().getTransferCodePrefix() + "|" + new Date() + "|" + new Date().getTime();
				
		String sql = "INSERT INTO  "
				+ "ALT_REJECTED_ITEM (TRANS_REF, SENDER_ACCT_NO, RECEIVER_ACCT_NO, AMOUNT, ERROR_CODE, ERROR_DESC, STATUS, TRANS_METHOD, CHARGE_AMOUNT, TAX_AMOUNT) "
				+ " VALUES(?,?,?,?,?,?,?,?,?,?)";

		// create the parameter values in the order needed by the insert statement
		List<ValueDatatypePair> param = new ArrayList<ValueDatatypePair>();
		param.add(new ValueDatatypePair(transactionReference, Types.VARCHAR));
		param.add(new ValueDatatypePair(senderAcctNo, Types.VARCHAR));
		param.add(new ValueDatatypePair(receiverAcctNo, Types.VARCHAR));
		param.add(new ValueDatatypePair(transactionAmount, Types.DOUBLE));
		param.add(new ValueDatatypePair(errorCode, Types.VARCHAR));
		param.add(new ValueDatatypePair(errorDesc, Types.VARCHAR));
		param.add(new ValueDatatypePair("R", Types.VARCHAR));
		param.add(new ValueDatatypePair(transactionMethod, Types.VARCHAR));
		param.add(new ValueDatatypePair(chargeAmount, Types.DOUBLE));
		param.add(new ValueDatatypePair(taxAmount, Types.DOUBLE));
		
		return executeDML(sql, param);
	}
	
	
	// older implementation
	public DBResponse _ProcedureXAPI_POSTING_SERVICE(final String PV_CHANNEL_CD,
			String PV_CHANNEL_PWD, String PV_DEVICE_ID, String PV_SERVICE_CD,
			String PV_TRANS_TYPE_CD, String PV_ACCOUNT_NO,
			String PV_ACCOUNT_CURRENCY_CD, BigDecimal PV_ACCOUNT_AMOUNT,
			String PV_TRANS_CURRENCY_CD, BigDecimal PV_TRANS_AMOUNT,
			Date PV_VALUE_DATE, Date PV_TRANS_DATE, String PV_TRANS_REF,
			String PV_SUPPLEMENTARY_REF, String PV_NARRATIVE,
			String PV_CONTRA_ACCOUNT_NO, String PV_CONTRA_CURRENCY_CD,
			BigDecimal PV_CONTRA_AMOUNT, BigDecimal PV_EXCHANGE_RATE,
			String PV_CHARGE_CD, BigDecimal PV_CHARGE_AMOUNT,
			String PV_TAX_CD, BigDecimal PV_TAX_AMOUNT,
			String PV_REVERSAL_FLG, String PV_ORIGIN_BANK_CD,
			String PV_SERVICE_PROVIDER, String PV_SERVICE_PROVIDER_SVC,
			String PV_USER_TYPE, String PV_USER_ID, String PV_USER_PWD,
			String PV_TRANS_TIME, String PV_EXTERNAL_COMMIT, String PV_PREVENT_DUPLICATES, 
			String PV_REQUEST_PWD, String PV_LOG_REPOSITORY, int PV_TEST_CALL_LEVEL,
			int PV_TRACE_LEVEL, String PV_ERROR_CODE, int PV_ERROR_SEVERITY,
			String PV_ERROR_MESSAGE
			 
			 
			) throws Exception {
		
		

		String XAPI_TRANS_SERVICE = "{call XAPI_POSTING_SERVICE_V2(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

		Connection dbConnection = null;
		dbConnection = databaseConnection();

		CallableStatement callableStatement = null;
		DBResponse dbResponse = new DBResponse();
			
		try {
			callableStatement = dbConnection.prepareCall(XAPI_TRANS_SERVICE);
			
			callableStatement.setString(1, PV_CHANNEL_CD); // PV_CHANNEL_CD
			callableStatement.setString(2, PV_CHANNEL_PWD); // PV_CHANNEL_PWD
			callableStatement.setString(3, PV_DEVICE_ID); // PV_DEVICE_ID
			callableStatement.setString(4, PV_SERVICE_CD); // PV_SERVICE_CD
			callableStatement.setString(5, PV_TRANS_TYPE_CD); // PV_TRANS_TYPE_CD
			callableStatement.setString(6, PV_ACCOUNT_NO); // PV_ACCOUNT_NO
			callableStatement.setString(7, PV_ACCOUNT_CURRENCY_CD); // PV_ACCOUNT_CURRENCY_CD
			callableStatement.setBigDecimal(8, PV_ACCOUNT_AMOUNT); // PV_ACCOUNT_AMOUNT
			callableStatement.setString(9, PV_TRANS_CURRENCY_CD); // PV_TRANS_CURRENCY_CD
			callableStatement.setBigDecimal(10, PV_TRANS_AMOUNT); // PV_TRANS_AMOUNT

			callableStatement.setDate(11, getCurrentDate()); // PV_VALUE_DATE
			callableStatement.setDate(12, getCurrentDate());// (12,// getCurrentDate());// //PV_TRANS_DATE
			callableStatement.setString(13, PV_TRANS_REF); // PV_TRANS_REF
			callableStatement.setString(14, PV_SUPPLEMENTARY_REF); // PV_SUPPLEMENTARY_REF
			callableStatement.setString(15, PV_NARRATIVE); // PV_NARRATIVE
			callableStatement.setString(16, PV_CONTRA_ACCOUNT_NO);// (16, "");// //PV_CONTRA_ACCOUNT_NO
			callableStatement.setString(17, PV_CONTRA_CURRENCY_CD); // PV_CONTRA_CURRENCY_CD
			callableStatement.setBigDecimal(18, PV_CONTRA_AMOUNT); // PV_CONTRA_AMOUNT// BigDecimal
			callableStatement.setBigDecimal(19, PV_EXCHANGE_RATE); // PV_EXCHANGE_RATE// BigDecimal
			callableStatement.setString(20, PV_CHARGE_CD); // PV_CHARGE_CD

			callableStatement.setBigDecimal(21, PV_CHARGE_AMOUNT); // PV_CHARGE_AMOUNT // BigDecimal
			callableStatement.setString(22, PV_TAX_CD);	//PV_TAX_CD               IN STRING,    
			callableStatement.setBigDecimal(23, PV_TAX_AMOUNT);	//PV_TAX_AMOUNT           IN NUMBER,
			callableStatement.setString(24, PV_REVERSAL_FLG); // PV_REVERSAL_FLG
			callableStatement.setNull(25, Types.VARCHAR); // PV_ORIGIN_BANK_CD
			callableStatement.setNull(26, Types.VARCHAR); // PV_SERVICE_PROVIDER
			callableStatement.setNull(27, Types.VARCHAR); // PV_SERVICE_PROVIDER_SVC
			callableStatement.setString(28, PV_USER_TYPE); // PV_USER_TYPE
			callableStatement.setString(29, PV_USER_ID); // PV_USER_ID
			callableStatement.setString(30, PV_USER_PWD); // PV_USER_PWD
			
			callableStatement.setString(31, PV_TRANS_TIME); //PV_TRANS_TIME           IN STRING,
			callableStatement.setString(32, PV_EXTERNAL_COMMIT); //PV_EXTERNAL_COMMIT      IN STRING,
			callableStatement.setString(33, PV_PREVENT_DUPLICATES);	//PV_PREVENT_DUPLICATES   IN STRING,
			callableStatement.setString(34, PV_REQUEST_PWD);	//PV_REQUEST_PWD          IN STRING,    
			callableStatement.setString(35, PV_LOG_REPOSITORY);	//PV_REQUEST_PWD 
			
			callableStatement.registerOutParameter(35, java.sql.Types.VARCHAR);  //PV_LOG_REPOSITORY       IN OUT STRING,
			callableStatement.registerOutParameter(36, java.sql.Types.NUMERIC); 	//PV_TEST_CALL_LEVEL      IN OUT NUMBER,      
			callableStatement.registerOutParameter(37, java.sql.Types.NUMERIC); // PV_TRACE_LEVEL	PV_TRACE_LEVEL          IN OUT NUMBER,   
			callableStatement.registerOutParameter(38, java.sql.Types.VARCHAR); // PV_ERROR_CODE	PV_ERROR_CODE           IN OUT STRING,    
			callableStatement.registerOutParameter(39, java.sql.Types.NUMERIC); // PV_ERROR_SEVERITY
			callableStatement.registerOutParameter(40, java.sql.Types.VARCHAR); // PV_ERROR_MESSAGE

			callableStatement.executeUpdate();

			int PV_ERROR_SEVERITY_lc = -1;
			
			int PV_TEST_CALL_LEVEL_lc = callableStatement.getInt(36);
			String PV_ERROR_CODE_lc = callableStatement.getString(38);
			PV_ERROR_SEVERITY_lc = callableStatement.getInt(39);
			String PV_ERROR_MESSAGE_lc = callableStatement.getString(40);
			int PV_TRACE_LEVEL_lc = callableStatement.getInt(37);
			String PV_LOG_REPOSITORY_lc = callableStatement.getString(35);

			dbResponse.PV_TEST_CALL_LEVEL = Integer.toString(PV_TEST_CALL_LEVEL_lc);
			dbResponse.PV_ERROR_CODE = PV_ERROR_CODE_lc;
			dbResponse.PV_ERROR_SEVERITY = Integer.toString(PV_ERROR_SEVERITY_lc);
			dbResponse.PV_ERROR_MESSAGE = PV_ERROR_MESSAGE_lc;
			dbResponse.PV_TRACE_LEVEL = Integer.toString(PV_TRACE_LEVEL_lc);
			dbResponse.PV_LOG_REPOSITORY = PV_LOG_REPOSITORY_lc;

			return dbResponse;

		} catch (SQLException e) {
			return dbResponse;
		} finally {

			if (callableStatement != null) {
				callableStatement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
	}
	
	// older implementation
	public int _saveRecordOld(final DBRequest dbRequest, final String responseCd, final String paymentReference, final String trans_method_name,
			final String trans_type, final String channelCode, final String Curr) throws Exception {
		
		String Isreversal = dbRequest.getNarration().startsWith("(Reversal)") ?  "True" : "False";
		
		//String content = CommonMethods.getInfo("core_systeminfo.txt", IntegrationSoapImpl.class);
		//content = CypherCrypt.deCypher(content);
		
		//String[] ipAndPort = content.split(",");
		//String	channelCode = ipAndPort[4].split("=>")[1].trim();
		//String Curr = ipAndPort[8].split("=>")[1].trim();

		Connection dbConnection = null;
		dbConnection = databaseConnection();

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			String sql = "INSERT INTO  "
					+ "ALT_QUICKTELLER(TRAN_APPL,TRAN_REF,FROM_ACCT_NUM,TRAN_RECEIVER,TRAN_AMOUNT,TRAN_STATUS,TRAN_NETHOD,NARRATION,TRAN_PURPOSE,CHANNEL_NAME ,ISREVERSAL,TRAN_DATE ,TRAN_TYPE ,PAYMENT_CURR, CHARGE_AMOUNT,TAX_AMOUNT) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			pst = dbConnection.prepareStatement(sql);
			pst.setString(1, dbRequest.getInitiatingApp()); // TRAN_APPL
			pst.setString(2, paymentReference); // TRAN_REF
			pst.setString(3, dbRequest.getSenderAcctNo()); // FROM_ACCT_NUM
			pst.setString(4, dbRequest.getBeneficiaryAcctNo()); // TRAN_RECEIVER
			pst.setDouble(5, Double.valueOf(dbRequest.getAmount())); // TRAN_AMOUNT
			pst.setString(6, responseCd); // TRAN_STATUS
			pst.setString(7, trans_method_name); // TRAN_NETHOD
			pst.setString(8, dbRequest.getNarration()); // NARRATION
			pst.setString(9, ""); // TRAN_PURPOSE
			pst.setString(10, channelCode); // CHANNEL_NAME
			pst.setString(11, Isreversal); // ISREVERSAL
			pst.setDate(12, getCurrentDate()); // TRAN_DATE
			pst.setString(13, trans_type); // TRAN_TYPE
			pst.setString(14, Curr); // PAYMENT_CURR	,		
			pst.setString(15, dbRequest.getChargeAmount()); //CHARGE_AMOUNT
			pst.setString(16, dbRequest.getTaxAmount()); //TAX_AMOUNT
			int result = pst.executeUpdate();
			
			System.out.println("Table ALT_QUICKTELLER Insert successful");
			
			return result;
		}

		catch (Exception e) {
			// E.printStackTrace();
			System.out.println("Table ALT_QUICKTELLER Insert failed");
			return -1;
		} finally {

			if (dbConnection != null) {
				dbConnection.close();
			}
			if (pst != null) {
				pst.close();
			}
			if (rs != null) {
				rs.close();
			}
		}

	}
	
	
	
	//*** END of very specific methods
	
	

	
	
	
	protected static java.sql.Date getCurrentDate() {
	    java.util.Date today = new java.util.Date();
	    //System.out.println("dateString1: " + new java.sql.Date(today.getTime()));
	    return new java.sql.Date(today.getTime());
	}
	
	protected static java.sql.Date getCurrentDate(final String inputDate, final String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date javaDate = getCurrentDate();
		
		try {
			if (inputDate.trim().length() > 0)
				javaDate = sdf.parse(inputDate);
			
		} catch (ParseException e) {}		
	    return new java.sql.Date(javaDate.getTime());
	}
	
	protected static java.sql.Date getCurrentDate(final String inputDate) {		
	    return getCurrentDate(inputDate, "dd/MM/yyyy");
	}
	
	protected static java.sql.Date getCurrentTimestamp(final String inputDate, final String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date javaDate = getCurrentDate();
		
		try {
			javaDate = sdf.parse(inputDate);
		} catch (ParseException e) {}
		
	    return new java.sql.Date(javaDate.getTime());
	}
	
	protected static java.sql.Date getCurrentTimestamp(final String inputDate) {
	    return getCurrentTimestamp(inputDate, "dd/MM/yyyy HH:mm:ss a");
	} 
	
	
	
	private String getDatabaseDriver(final String databaseType) {
		String driver = "";
		switch(databaseType.toUpperCase()) {
		case ResponseConstants.ORACLE_DATABASE : 
			driver = ResponseConstants.ORACLE_DRIVER;
			break;
		case ResponseConstants.SYBASE_DATABASE : 
			driver = ResponseConstants.SYBASE_DRIVER;
			break;
		}
		
		return driver;
	}

	private String getDatabaseConnectionUrl(final String databaseType, final String ipAddress, final String portNo, final String serviceName) {
		String connectionUrl = "";
		switch(databaseType.toUpperCase()) {
		case ResponseConstants.ORACLE_DATABASE : 
			connectionUrl = ResponseConstants.ORACLE_CONNECTION_URL_PREFIX + ipAddress + ":" + portNo + "/" + serviceName;
			break;
		case ResponseConstants.SYBASE_DATABASE : 
			connectionUrl = ResponseConstants.SYBASE_CONNECTION_URL_PREFIX + ipAddress + ":" + portNo + "/" + serviceName;
			break;
		}
		//10.152.2.32:5000/banking
		return connectionUrl;
	}
	
	public static Database readConfig() {
		Database database = new Database();
		
		try {
			String content = CommonMethods.getInfo("DatabaseInfo.xml", DBConnection.class);
			
			database = CommonMethods.xmlStringToObject(content, Database.class);
			database = decryptContent(database);
			
			database.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		} catch (Exception e) {
			System.out.println("Cannot read DatabaseInfo.xml");
			database.setResponseCode(ResponseConstants.FILE_ERROR_CODE);
		}
		
		return database;
	}
	
	private static Database decryptContent(final Database database) {
		
		Database databaseDup  = database;
		try {
			if (!database.getDatabaseProps().isEmpty())
				for (DatabaseProperty dbProperty : database.getDatabaseProps()) {
					String defaultDB = CypherCrypt.deCypher(dbProperty.getDefaultDB().trim()) == null || CypherCrypt.deCypher(dbProperty.getDefaultDB().trim()).equals("")
							? dbProperty.getDefaultDB() : CypherCrypt.deCypher(dbProperty.getDefaultDB());
					
					String alias = CypherCrypt.deCypher(dbProperty.getAlias().trim()) == null || CypherCrypt.deCypher(dbProperty.getAlias().trim()).equals("")
							? dbProperty.getAlias() : CypherCrypt.deCypher(dbProperty.getAlias());
							
					String type = CypherCrypt.deCypher(dbProperty.getType().trim()) == null || CypherCrypt.deCypher(dbProperty.getType().trim()).equals("")
							? dbProperty.getType() : CypherCrypt.deCypher(dbProperty.getType());
							
					String username = CypherCrypt.deCypher(dbProperty.getUsername().trim()) == null || CypherCrypt.deCypher(dbProperty.getUsername().trim()).equals("")
							? dbProperty.getUsername() : CypherCrypt.deCypher(dbProperty.getUsername());
							
					String password = CypherCrypt.deCypher(dbProperty.getPassword().trim()) == null || CypherCrypt.deCypher(dbProperty.getPassword().trim()).equals("")
							? dbProperty.getPassword() : CypherCrypt.deCypher(dbProperty.getPassword());
							
					String ipAddress = CypherCrypt.deCypher(dbProperty.getIpAddress().trim()) == null || CypherCrypt.deCypher(dbProperty.getIpAddress().trim()).equals("")
							? dbProperty.getIpAddress() : CypherCrypt.deCypher(dbProperty.getIpAddress());
							
					String portNumber = CypherCrypt.deCypher(dbProperty.getPortNumber().trim()) == null || CypherCrypt.deCypher(dbProperty.getPortNumber().trim()).equals("")
							? dbProperty.getPortNumber() : CypherCrypt.deCypher(dbProperty.getPortNumber());
							
					String serviceName = CypherCrypt.deCypher(dbProperty.getServiceName().trim()) == null || CypherCrypt.deCypher(dbProperty.getServiceName().trim()).equals("")
							? dbProperty.getServiceName() : CypherCrypt.deCypher(dbProperty.getServiceName());
					
					dbProperty.setDefaultDB(defaultDB);
					dbProperty.setAlias(alias);
					dbProperty.setType(type);
					dbProperty.setUsername(username);
					dbProperty.setPassword(password);
					dbProperty.setIpAddress(ipAddress);
					dbProperty.setPortNumber(portNumber);
					dbProperty.setServiceName(serviceName);
				}

			return database;
		} catch (Exception e) {
			System.out.println("DatabaseCredential: \n" + CommonMethods.objectToXml(database));
			System.out.println("Cannot decrypt content");
			
			return databaseDup;
		}
		
	}
	
	
	
	
}
