package com.company.application.device.data;

public class RegisterDeviceResponse extends DeviceLoginResponse{
	
	private String initiatingApp;
	
	public RegisterDeviceResponse() {}
	
	public RegisterDeviceResponse(String responseCode, String responseMessage) {
		super(responseCode, responseMessage);
	}
	
	public String getInitiatingApp() {
		return this.initiatingApp;
	}
	
	public void setInitiatingApp(String initiatingApp) {
		this.initiatingApp = initiatingApp;
	}
	
	
}
