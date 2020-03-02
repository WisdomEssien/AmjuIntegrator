package com.company.application.device.data;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class DeviceLoginResponse extends ResponseModel{

	private String accountNumber;
	private String phoneNo;
	private String deviceUIID;
	private String password;
	private String accessPin;
	private String lastlogin;
	
	public DeviceLoginResponse() {}
	
	public DeviceLoginResponse(String responseCode, String responseMessage) {
		super(responseCode, responseMessage);
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public String getPhoneNo() {
		return phoneNo;
	}
	
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	public String getDeviceUIID() {
		return deviceUIID;
	}
	
	public void setDeviceUIID(String deviceUIID) {
		this.deviceUIID = deviceUIID;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getAccessPin() {
		return accessPin;
	}
	
	public void setAccessPin(String accessPin) {
		this.accessPin = accessPin;
	}
	
	public String getLastlogin() {
		return lastlogin;
	}
	
	public void setLastlogin(String lastlogin) {
		this.lastlogin = lastlogin;
	}
	
}
