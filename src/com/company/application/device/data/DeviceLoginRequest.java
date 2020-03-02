package com.company.application.device.data;

public class DeviceLoginRequest {

	private String accountNumber;
	private String deviceUIID;
	private String password;
	private String appVersion;
	private String currentDate;
	
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
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

	public String getAppVersion() {
		return appVersion == null ? "" : appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getCurrentDate() {
		return currentDate == null ? "" : currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}
	
	
}
