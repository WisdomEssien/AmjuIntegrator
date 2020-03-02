package com.neptunesoftware.reuseableClasses.Quickteller.data;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MailAddresses")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailAddress {

	@XmlElement(name = "EmailAddress")
	private List<String> emailAddress;

	public List<String> getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(List<String> emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	
}
