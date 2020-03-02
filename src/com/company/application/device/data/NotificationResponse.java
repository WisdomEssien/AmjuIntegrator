package com.company.application.device.data;

import java.util.List;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class NotificationResponse extends ResponseModel{

	private List<Notification> notifications;

	public NotificationResponse() {
	}
	
	public NotificationResponse(List<Notification> notifications) {
		super();
		this.notifications = notifications;
	}
	
	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	
	
}
