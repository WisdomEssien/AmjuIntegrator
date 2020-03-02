package com.company.application.device.data;

public class ChangeCredential extends DeviceLoginRequest{
	
	private String newPassword;
	private String pin;
	private String newPin;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldPin() {
		return pin;
	}

	public void setOldPin(String pin) {
		this.pin = pin;
	}

	public String getNewPin() {
		return newPin;
	}

	public void setNewPin(String newPin) {
		this.newPin = newPin;
	}
	
	
	
}
