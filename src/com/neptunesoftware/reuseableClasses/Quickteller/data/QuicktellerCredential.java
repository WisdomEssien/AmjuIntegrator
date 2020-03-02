package com.neptunesoftware.reuseableClasses.Quickteller.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "QuicktellerInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuicktellerCredential {

	@XmlTransient
	private String responseCode;
	
	@XmlElement(name = "BaseUrl")
	private String baseUrl;
	
	@XmlElement(name = "ClientId")
	private String clientId;
	
	@XmlElement(name = "ClientSecret")
	private String clientSecret;
	
	@XmlElement(name = "TransferCodePrefix")
	private String transferCodePrefix;
	
	@XmlElement(name = "TerminalId")
	private String terminalId;
	
	@XmlElement(name = "InitiatingEntityCode")
	private String initiatingEntityCode;
	
	@XmlElement(name = "SignatureMethod")
	private String signatureMethod;
	
	public String getResponseCode() {
		return responseCode == null ? "" : responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getBaseUrl() {
		return baseUrl == null ? "" : baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getClientId() {
		return clientId == null ? "" : clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret == null ? "" : clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getTransferCodePrefix() {
		return transferCodePrefix == null ? "" : transferCodePrefix;
	}

	public void setTransferCodePrefix(String transferCodePrefix) {
		this.transferCodePrefix = transferCodePrefix;
	}

	public String getTerminalId() {
		return terminalId == null ? "" : terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getInitiatingEntityCode() {
		return initiatingEntityCode == null ? "" : initiatingEntityCode;
	}

	public void setInitiatingEntityCode(String initiatingEntityCode) {
		this.initiatingEntityCode = initiatingEntityCode;
	}

	public String getSignatureMethod() {
		return signatureMethod == null ? "" : signatureMethod;
	}

	public void setSignatureMethod(String signatureMethod) {
		this.signatureMethod = signatureMethod;
	}
	
	
	
}
