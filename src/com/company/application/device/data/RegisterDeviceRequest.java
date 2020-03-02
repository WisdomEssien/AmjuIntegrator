package com.company.application.device.data;

public class RegisterDeviceRequest extends DeviceLoginRequest{
	
	private String accessPin;
	private String initiatingApp;
	private String phoneNumber;
	
	public String getAccessPin() {
		return accessPin;
	}
	
	public void setAccessPin(String accessPin) {
		this.accessPin = accessPin;
	}
	
	public String getInitiatingApp() {
		return initiatingApp;
	}
	
	public void setInitiatingApp(String initiatingApp) {
		this.initiatingApp = initiatingApp;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
	
}
