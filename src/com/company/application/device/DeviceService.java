package com.company.application.device;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.company.application.account.AccountService;
import com.company.application.device.data.ChangeCredential;
import com.company.application.device.data.DeviceLoginRequest;
import com.company.application.device.data.DeviceLoginResponse;
import com.company.application.device.data.NotificationResponse;
import com.company.application.device.data.OTPRequest;
import com.company.application.device.data.PossibleResponse;
import com.company.application.device.data.RegisterDeviceRequest;
import com.company.application.device.data.RegisterDeviceResponse;
import com.company.application.device.data.Response;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.MailService;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;

public class DeviceService {

	private static Date nextSendDate;
	
	static {
		nextSendDate = new Date();
	}
	
	public NotificationResponse notification() {
		System.out.println("\n**** In Notification ****");

		NotificationResponse notificationResponse = new NotificationResponse();
		
		DeviceDBOperation database = new DeviceDBOperation();
		notificationResponse.setNotifications(database.selectNotification());
		
		// success
		notificationResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		notificationResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);

		if (notificationResponse.getNotifications().equals(null)) {
			notificationResponse.setResponseCode(ResponseConstants.EXCEPTION_CODE);
			notificationResponse.setResponseMessage(ResponseConstants.EXCEPTION_MESSAGE);
			
		}else if (notificationResponse.getNotifications().isEmpty()) {
			notificationResponse.setResponseCode(ResponseConstants.NOT_FOUND_CODE);
			notificationResponse.setResponseMessage(ResponseConstants.NOT_FOUND_MESSAGE);
		}

		return notificationResponse;
	}
	
	public static void main(String[] args) {
		
		DeviceLoginRequest deviceLoginRequest = new DeviceLoginRequest();
		deviceLoginRequest.setAccountNumber("2002080634");
		deviceLoginRequest.setDeviceUIID("23a8dc4a20b248b0");
		deviceLoginRequest.setPassword("J3VWANNHTEQsQsnMZCr4Kg==");
		deviceLoginRequest.setAppVersion("0.2.4");
		//deviceLoginRequest.setNewPassword("J3VWANNHTEQsQsnMZCr4Kg==");
		//deviceLoginRequest.setOldPin("gRqVWTbO9oJuEJOa8ZKGjA==");
		//deviceLoginRequest.setNewPin("gRqVWTbO9oJuEJOa8ZKGjA==");
				
		System.out.println(CommonMethods.ObjectToJsonString(deviceLoginRequest));
		
		ResponseModel deveiceLoginResponse = new DeviceService().login(CommonMethods.ObjectToJsonString(deviceLoginRequest));
		System.out.println(CommonMethods.ObjectToJsonString(deveiceLoginResponse));
		

//		String body = "{\"phoneNo\":\"+2348050827453\",\"OTP\":\"48262\"}";		
//		System.out.println(CommonMethods.ObjectToJsonString(new DeviceService().saveOTP(body)));
		
//		String body = "{\r\n" + 
//				"	\"accessPin\": \"1234\",\r\n" + 
//				"	\"initiatingApp\": \"MAPP\",\r\n" + 
//				"	\"phoneNumber\": \"+234555888999\",\r\n" + 
//				"	\"accountNumber\": \"2002077839\",\r\n" + 
//				"	\"deviceUIID\": \"12345678UIID\",\r\n" + 
//				"	\"password\": \"password\"\r\n" + 
//				"}";		
//		System.out.println(CommonMethods.ObjectToJsonString(new DeviceService().register(body)));
		
		//System.out.println("Another Device: " + new DeviceDBOperation().isAnotherDevice("2002077839", "1234567UIID"));
		
	}
	
	public DeviceLoginResponse login(String body){
		System.out.println("\n**** In Login Device ****");

		DeviceDBOperation database = new DeviceDBOperation();
		
		DeviceLoginRequest deviceLoginRequest = (DeviceLoginRequest) CommonMethods.JSONStringToObject(body, DeviceLoginRequest.class);

		DeviceLoginResponse deviceLoginResponse = new DeviceLoginResponse();
		
		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();
		
		// this is to ensure another device can not be used to login
		// if the user is already registered
		if(database.isAnotherDevice(deviceLoginRequest.getAccountNumber(), deviceLoginRequest.getDeviceUIID())) {
			
			CommonMethods.logSensitiveContent(" ** Login request ** \n" + body + "\n\r\n\r"
					+ " ** Login response ** \n" + CommonMethods.ObjectToJsonString(new RegisterDeviceResponse(ResponseConstants.DEVICE_CODE, ResponseConstants.DEVICE_MESSAGE)));
			
			return new RegisterDeviceResponse(ResponseConstants.DEVICE_CODE, ResponseConstants.DEVICE_MESSAGE);
		}
		
		// return if user is using an older version which is less than the compatible one
		if (!rubikonCredential.getAppVersion().equals("0")) {
			//if (!deviceLoginRequest.getAppVersion().equals(rubikonCredential.getAppVersion())) {
			//	return new DeviceLoginResponse(ResponseConstants.VERSION_CODE, ResponseConstants.VERSION_MESSAGE);
			//}
			if (!(deviceLoginRequest.getAppVersion().compareTo(rubikonCredential.getAppVersion()) >= 0)) {
				
				CommonMethods.logSensitiveContent(" ** Login request ** \n" + body + "\n\r\n\r"
						+ " ** Login response ** \n" + CommonMethods.ObjectToJsonString(new RegisterDeviceResponse(ResponseConstants.VERSION_CODE, ResponseConstants.VERSION_MESSAGE)));
				
				return new DeviceLoginResponse(ResponseConstants.VERSION_CODE, ResponseConstants.VERSION_MESSAGE);
			}
		}
		
		Date currentDate = CommonMethods.convertDateFormatToDate(deviceLoginRequest.getCurrentDate(), "MM/dd/yyyy", "yyyyMMdd");
		Date licenceExpiryDate = CommonMethods.getCurrentDateAsDate(rubikonCredential.getLincenceInfo(), "yyyyMMdd");
		int alertPeriodInDays = Integer.valueOf(rubikonCredential.getAlertPeriodInDays()) <= 0 ? 15 : Integer.valueOf(rubikonCredential.getAlertPeriodInDays());
		
		int gracePeriodInDays = Integer.valueOf(rubikonCredential.getGracePeriodInDays());
		Date licenceExpiryDatePlusGracePeriod = CommonMethods.addDays(licenceExpiryDate, "yyyyMMdd", gracePeriodInDays);
		
		int remainingDays = Days.daysBetween(new DateTime(currentDate), new DateTime(licenceExpiryDate)).getDays();
		int remainingDaysPlusGracePeriod = Days.daysBetween(new DateTime(currentDate), new DateTime(licenceExpiryDatePlusGracePeriod)).getDays();
		
//		System.out.println("currentDate: " + currentDate);
//		System.out.println("licenceExpiryDate: " + licenceExpiryDate);
//		System.out.println("gracePeriodInDays: " + gracePeriodInDays);
//		System.out.println("licenceExpiryDatePlusGracePeriod: " + licenceExpiryDatePlusGracePeriod);
//		System.out.println("remainingDays: " + remainingDays);
//		System.out.println("remainingDaysPlusGracePeriod: " + remainingDaysPlusGracePeriod);
		
		
		if(remainingDays <= alertPeriodInDays) {
//			// This is to ensure alert is sent once in a day except the server is restarted			
//			if(Days.daysBetween(new DateTime(currentDate), new DateTime(nextSendDate)).getDays() <= 0) {
//				// send alert to ALL GRANT's login roles
//				List<Long> userRoleIds = database.selectUserRoles("GRANT051");
//				for(Long userRoleId : userRoleIds) {
//					//database.generateAndSendAlert(userRoleId, 7964, remainingDays); //userRoleId 22049
//				}
//				nextSendDate = CommonMethods.addDays(currentDate, "yyyyMMdd", 1);
//				System.out.println("nextSendDate: " + nextSendDate);
//			}
			
			
			// This is to ensure mail is sent once in a day except the server is restarted
			if(Days.daysBetween(new DateTime(currentDate), new DateTime(nextSendDate)).getDays() <= 0) {
				String mailSubject = "Mobile App channel Licence Expiration";
				String mailContent = remainingDays <= 0 
						? "Dear Amju Unique MFB,\r\n\r\n" + 
						"Your licence for AmjuMobile app has expired. Please contact Neptune Software Limited.\r\n\r\n" + 
						"Thanks for your usual Patronage.\r\n\r\n" + 
						"Regards"
				
						: "Dear Amju Unique MFB,\r\n\r\n" + 
						"Please note that your licence for AmjuMobile app will expire in "+remainingDays+" day(s) time.\r\n\r\n" + 
						"Thanks for your usual Patronage.\r\n\r\n" + 
						"Regards";
				
				String senderMail = "no-reply@neptunesoftwaregroup.com";
				
				MailService mailService = new MailService()
						.host("secure.emailsrvr.com")
						.port("465")
						.username("wisdomessien@neptunesoftwaregroup.com")
						.password("NewPa55w0rd");
				
				
				// send mail to all recipients
				List<String> mailRecipients = rubikonCredential.getMailRecipient().getEmailAddress();
				for (String receiver : mailRecipients) {
					mailService.sendMail(senderMail, receiver.trim(), mailSubject, mailContent);
				}
				
//				mailService.sendMail("no-reply@neptunesoftwaregroup.com", "olawumioladimeji@neptunesoftwaregroup.com", mailSubject, mailContent);
//				mailService.sendMail("no-reply@neptunesoftwaregroup.com", "grant.aghedo@amjuuniquemfbng.com", mailSubject, mailContent);
//				mailService.sendMail("no-reply@neptunesoftwaregroup.com", "wisdomessien@neptunesoftwaregroup.com", mailSubject, mailContent);	
			
				nextSendDate = CommonMethods.addDays(currentDate, "yyyyMMdd", 1);
				System.out.println("nextSendDate: " + nextSendDate);
			}
			
		}
		
		if(remainingDaysPlusGracePeriod <= 0) {
			
			CommonMethods.logSensitiveContent(" ** Login request ** \n" + body + "\n\r\n\r"
					+ " ** Login response ** \n" + CommonMethods.ObjectToJsonString(new RegisterDeviceResponse(ResponseConstants.LICENCE_CODE, ResponseConstants.LICENCE_MESSAGE)));
			
			return new DeviceLoginResponse(ResponseConstants.LICENCE_CODE, ResponseConstants.LICENCE_MESSAGE);
		}
		
		deviceLoginResponse = database.selectLoginDetails(deviceLoginRequest.getAccountNumber(), deviceLoginRequest.getPassword(), deviceLoginRequest.getDeviceUIID());
		
		if (deviceLoginResponse == null) {
			
			CommonMethods.logSensitiveContent(" ** Login request ** \n" + body + "\n\r\n\r"
					+ " ** Login response ** \n" + CommonMethods.ObjectToJsonString(new RegisterDeviceResponse(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE)));
			
			return new DeviceLoginResponse(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE);
		}
		
		return deviceLoginResponse;
	}
	
	public RegisterDeviceResponse register(String body) {
		System.out.println("\n**** In Register Device ****");
		
		RegisterDeviceRequest registerDeviceRequest = (RegisterDeviceRequest) CommonMethods.JSONStringToObject(body, RegisterDeviceRequest.class);

		// validate that account exist
		if (!accountExist(registerDeviceRequest.getAccountNumber())) {
			CommonMethods.logSensitiveContent(" ** Register Device request ** \n" + body + "\n\r\n\r"
					+ " ** Register Device response ** \n" + CommonMethods.ObjectToJsonString(new RegisterDeviceResponse(ResponseConstants.NOT_FOUND_CODE, ResponseConstants.NOT_FOUND_MESSAGE)));
			
			return new RegisterDeviceResponse(ResponseConstants.NOT_FOUND_CODE, ResponseConstants.NOT_FOUND_MESSAGE);
		}
		
		RegisterDeviceResponse registerDeviceResponse = new RegisterDeviceResponse();
		
		DeviceDBOperation database = new DeviceDBOperation();
		
//		// this is to ensure another device can not be used to register again
//		// if the user is already registered
//		if(database.isAnotherDevice(registerDeviceRequest.getAccountNumber(), registerDeviceRequest.getDeviceUIID().trim())) {
//			
//			CommonMethods.logSensitiveContent(" ** Register Device request ** \n" + body + "\n\r\n\r"
//					+ " ** Register Device response ** \n" + CommonMethods.ObjectToJsonString(new RegisterDeviceResponse(ResponseConstants.DEVICE_CODE, ResponseConstants.DEVICE_MESSAGE)));
//			
//			return new RegisterDeviceResponse(ResponseConstants.DEVICE_CODE, ResponseConstants.DEVICE_MESSAGE);
//		}
		
		registerDeviceResponse = database.register(registerDeviceRequest.getAccountNumber(), registerDeviceRequest.getPhoneNumber(),
									registerDeviceRequest.getDeviceUIID(), registerDeviceRequest.getPassword(), registerDeviceRequest.getAccessPin());
		
		if (registerDeviceResponse == null) {
			
			CommonMethods.logSensitiveContent(" ** Register Device request ** \n" + body + "\n\r\n\r"
					+ " ** Register Device response ** \n" + CommonMethods.ObjectToJsonString(new RegisterDeviceResponse(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE)));
			
			return new RegisterDeviceResponse(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE);
		}
				
		CommonMethods.logSensitiveContent(" ** Register Device request ** \n" + body + "\n\r\n\r"
				+ " ** Register Device response ** \n" + CommonMethods.ObjectToJsonString(registerDeviceResponse));
		
		return registerDeviceResponse;
	}
	
	public ResponseModel saveOTP(String body){
		System.out.println("\n**** In Save OTP ****");
		
		OTPRequest otpRequest = (OTPRequest) CommonMethods.JSONStringToObject(body, OTPRequest.class);
		
		ResponseModel responseModel = new ResponseModel();
		
		DeviceDBOperation database = new DeviceDBOperation();
		responseModel = database.saveOTP(otpRequest.getPhoneNumber(), otpRequest.getOTP());
		
		String message = "Your OTP is " + otpRequest.getOTP();
		database.saveSMS("", otpRequest.getPhoneNumber(), message, "OTP");

		// this was created to obfusticate the OTP generated before printing it out
		OTPRequest otpRequestDup = new OTPRequest();
		otpRequestDup.setPhoneNumber(otpRequest.getPhoneNumber());
		otpRequestDup.setOTP("****");
		CommonMethods.logSensitiveContent(" ** OTP request ** \n" + CommonMethods.ObjectToJsonString(otpRequestDup) + "\n\r\n\r"
						+ " ** OTP response ** \n" + CommonMethods.ObjectToJsonString(responseModel));
		
		return responseModel;
	}
	
	public DeviceLoginResponse resetCredential(String body) {
		System.out.println("\n**** In Reset Password ****");
		
		DeviceLoginRequest loginCredential = (DeviceLoginRequest) CommonMethods.JSONStringToObject(body, DeviceLoginRequest.class);
		
		// validate that account exist
		if (!accountExist(loginCredential.getAccountNumber())) {
			
			CommonMethods.logSensitiveContent(" ** forget password request ** \n" + body + "\n\r\n\r"
					+ " ** forget password response ** \n" + CommonMethods.ObjectToJsonString(new DeviceLoginResponse(ResponseConstants.NOT_FOUND_CODE, ResponseConstants.NOT_FOUND_CODE)));
						
			return new DeviceLoginResponse(ResponseConstants.NOT_FOUND_CODE, ResponseConstants.NOT_FOUND_MESSAGE);
		}
		
		DeviceLoginResponse deviceLoginResponse = new DeviceLoginResponse();
		
		DeviceDBOperation database = new DeviceDBOperation();
		
//		// this is to ensure another device can not be used to reset password
//		// if the user is already registered
//		if(database.isAnotherDevice(loginCredential.getAccountNumber(), loginCredential.getDeviceUIID().trim())) {
//			
//			CommonMethods.logSensitiveContent(" ** forget password request ** \n" + body + "\n\r\n\r"
//					+ " ** forget password response ** \n" + CommonMethods.ObjectToJsonString(new DeviceLoginResponse(ResponseConstants.DEVICE_CODE, ResponseConstants.DEVICE_MESSAGE)));
//			
//			return new RegisterDeviceResponse(ResponseConstants.DEVICE_CODE, ResponseConstants.DEVICE_MESSAGE);
//		}
		
		deviceLoginResponse = database.selectLoginDetails(loginCredential.getAccountNumber(), loginCredential.getDeviceUIID());
		
		if (deviceLoginResponse == null) {
			
			CommonMethods.logSensitiveContent(" ** forget password request ** \n" + body + "\n\r\n\r"
					+ " ** forget password response ** \n" + CommonMethods.ObjectToJsonString(new DeviceLoginResponse(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE)));
			
			return new DeviceLoginResponse(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE);
		}
		
		CommonMethods.logSensitiveContent(" ** forget password request ** \n" + body + "\n\r\n\r"
				+ " ** forget password response ** \n" + CommonMethods.ObjectToJsonString(deviceLoginResponse));
		
		return deviceLoginResponse;
	}
		
	public ResponseModel changePassword(String body) {
		
		System.out.println("\n**** In Change Password ****");
		
		ChangeCredential changeCredential = (ChangeCredential) CommonMethods.JSONStringToObject(body, ChangeCredential.class);
				
		DeviceDBOperation database = new DeviceDBOperation();		
		DeviceLoginResponse deviceLoginResponse = database.selectLoginDetails(changeCredential.getAccountNumber(), changeCredential.getPassword(), changeCredential.getDeviceUIID());
		
		if (deviceLoginResponse == null) {
			return new ResponseModel(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE);
		}
		
		// probably invalid password
		if (!deviceLoginResponse.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE)) {
			return deviceLoginResponse;
		}
				
		ResponseModel responseModel = new ResponseModel(ResponseConstants.QUERY_CODE, ResponseConstants.QUERY_MESSAGE);
		int result = database.updatePassword(changeCredential.getAccountNumber(), changeCredential.getNewPassword(), changeCredential.getDeviceUIID());
		if (result > 0) {
			responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}
		
		return responseModel;
	}
	
	public ResponseModel changePin(String body) {
		
		System.out.println("\n**** In Change Pin ****");
		
		ChangeCredential changeCredential = (ChangeCredential) CommonMethods.JSONStringToObject(body, ChangeCredential.class);
				
		DeviceDBOperation database = new DeviceDBOperation();		
		DeviceLoginResponse deviceLoginResponse = database.selectLoginDetails(changeCredential.getAccountNumber(), changeCredential.getDeviceUIID());
		
		if (deviceLoginResponse == null) {
			return new ResponseModel(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE);
		}
		
		if (!deviceLoginResponse.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE)) {
			return deviceLoginResponse;
		}
		
		if (!deviceLoginResponse.getAccessPin().equals(changeCredential.getOldPin())) {
			return new ResponseModel(ResponseConstants.LOGIN_CODE, ResponseConstants.LOGIN_MESSAGE);
		}
		
		ResponseModel responseModel = new ResponseModel(ResponseConstants.QUERY_CODE, ResponseConstants.QUERY_MESSAGE);
		int result = database.updatePin(changeCredential.getAccountNumber(), changeCredential.getNewPin(), changeCredential.getDeviceUIID());
		if (result > 0) {
			responseModel.setResponseCode(ResponseConstants.SUCCEESS_CODE);
			responseModel.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		}
		
//		// log request and responses
//		CommonMethods.logContent("**** Change Pin Request**** \n" + body + "\r\n\r\n"
//							+ "*** Change Pin Response *** \n" + CommonMethods.ObjectToJsonString(responseModel));
				
		return responseModel;
	}
	
	public PossibleResponse possibleResponse() {
		
		List<Response> responses = new ArrayList<Response>();
		responses.add(new Response(ResponseConstants.SUCCEESS_CODE, ResponseConstants.SUCCEESS_MESSAGE, "Indicates success"));
		responses.add(new Response(ResponseConstants.INSUFFICIENT_CODE, ResponseConstants.INSUFFICIENT_MESSAGE, "The account balance returned for account number is lower than total deductions to be made"));
		responses.add(new Response(ResponseConstants.WEBSERVICE_UNAVAILABLE_CODE, ResponseConstants.WEBSERVICE_UNAVAILABLE_MESSAGE, "Indicates that calling thirdparty webservice endpoint failed"));
		responses.add(new Response(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE, "Custom message. Usually failed response received from calling thirdparty webservice endpoint", "Indicates that thirdparty webservice endpoint returned a failed response"));
		responses.add(new Response(ResponseConstants.PROCEDURE_CODE, ResponseConstants.PROCEDURE_MESSAGE, "Indicates failure when failing to execute procedure or when a failed response is gotten from executing the procedure"));
		responses.add(new Response(ResponseConstants.ALREADY_EXIST_CODE, ResponseConstants.ALREADY_EXIST_MESSAGE, "Indicates that a similar record already exist"));
		responses.add(new Response(ResponseConstants.NOT_FOUND_CODE, ResponseConstants.NOT_FOUND_MESSAGE, "Indicates that record searched for does not exist"));
		responses.add(new Response(ResponseConstants.QUERY_CODE, ResponseConstants.QUERY_MESSAGE, "Indicates a possiblity that a wrong SQL query was executed. check the source code"));
		responses.add(new Response(ResponseConstants.UNMARSHAL_CODE, ResponseConstants.UNMARSHAL_MESSAGE, "Indicates that an unusual response object was received from calling thirdparty webservice and therefore could not be converted to a java object"));
		responses.add(new Response(ResponseConstants.MANDATORY_CODE, ResponseConstants.MANDATORY_MESSAGE, "Indicates that a Required parameter was not passed"));
		responses.add(new Response(ResponseConstants.FILE_ERROR_CODE, ResponseConstants.FILE_ERROR_MESSAGE, "Could not read from specified file due to wrong directory or file not existing. check the source code"));
		responses.add(new Response(ResponseConstants.EXCEPTION_CODE, ResponseConstants.EXCEPTION_MESSAGE, "Indicates that a java exception was thrown while processing. check the source code"));
		responses.add(new Response(ResponseConstants.LOGIN_CODE, ResponseConstants.LOGIN_MESSAGE, "Indicates that wrong authentication credentials are passed. confirm that username or password or pin is correct"));
		responses.add(new Response(ResponseConstants.VERSION_CODE, ResponseConstants.VERSION_MESSAGE, "Indicates that there is a version discrepancy"));
		responses.add(new Response(ResponseConstants.LICENCE_CODE, ResponseConstants.LICENCE_MESSAGE, "Indicates that licence has expired"));
		responses.add(new Response(ResponseConstants.DEVICE_CODE, ResponseConstants.DEVICE_MESSAGE, "Indicates that there is device UIID discrepancy"));
		
		PossibleResponse possibleResponse = new PossibleResponse(responses);
		
		return possibleResponse;
	}
	
	
	
	private boolean accountExist(String accountNumber) {
		AccountService accountService = new AccountService();		
		if(accountService.accountInfo(accountNumber).getResponseCode().equals(ResponseConstants.SUCCEESS_CODE)) {
			return true;
		}
		return false;
	}
	

	
	
	
}
