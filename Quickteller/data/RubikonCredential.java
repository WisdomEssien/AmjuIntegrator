package com.neptunesoftware.reuseableClasses.Quickteller.data;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "RubikonInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class RubikonCredential {

	@XmlTransient
	private String responseCode;
	
	@XmlElement(name = "IPAddress")
	private String ipAddress;
	
	@XmlElement(name = "PortNumber")
	private String portNumber;
	
	@XmlElement(name = "ChannelId")
	private String channelId;
	
	@XmlElement(name = "ChannelCode")
	private String channelCode;
	
	@XmlElement(name = "TransactionFee")
	private String transactionFee;
	
	@XmlElement(name = "ChargeCode")
	private String chargeCode;
	
	@XmlElement(name = "TaxCode")
	private String taxCode;
	
	@XmlElement(name = "CurrencyCode")
	private String currencyCode;
	
	@XmlElement(name = "FundTransferCredit")
	private String fundTransferCredit;
	
	@XmlElement(name = "FundTransferDebit")
	private String fundTransferDebit;
	
	@XmlElement(name = "BillsPaymentCredit")
	private String billsPaymentCredit;
	
	@XmlElement(name = "BillsPaymentDebit")
	private String billsPaymentDebit;
	
	@XmlElement(name = "MobileRechargeCredit")
	private String mobileRechargeCredit;
	
	@XmlElement(name = "MobileRechargeDebit")
	private String mobileRechargeDebit;
	
	@XmlElement(name = "AuthenticatedUsername")
	private String authenticatedUsername;
	
	@XmlElement(name = "AuthenticatedPassword")
	private String authenticatedPassword;
	
	@XmlElement(name = "TransferLimitInternal")
	private String transferLimitInternal;
	
	@XmlElement(name = "TransferLimitExternal")
	private String transferLimitExternal;
	
	@XmlElement(name = "ApplicationUsername")
	private String applicationUsername;
	
	@XmlElement(name = "ChargeCodeBillsPayment")
	private String chargeCodeBillsPayment;
	
	@XmlElement(name = "CompatibleVersion")
	private String appVersion;
	
	@XmlElement(name = "LicenceInfo")
	private String lincenceInfo;
	
	@XmlElement(name = "AlertPeriodInDays")
	private String alertPeriodInDays;
	
	@XmlElement(name = "GracePeriodInDays")
	private String gracePeriodInDays;
	
	@XmlElement(name = "AuthenticateService")
	private String authenticateService;
	
	@XmlElement(name = "MailRecipient")
	private EmailAddress mailRecipient;
	
	public String getResponseCode() {
		return responseCode == null ? "" : responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getIpAddress() {
		return ipAddress == null ? "" : ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPortNumber() {
		return portNumber == null ? "" : portNumber;
	}

	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

	public String getChannelId() {
		return channelId == null ? "" : channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelCode() {
		return channelCode == null ? "" : channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getTransactionFee() {
		return transactionFee == null ? "" : transactionFee;
	}

	public void setTransactionFee(String transactionFee) {
		this.transactionFee = transactionFee;
	}

	public String getChargeCode() {
		return chargeCode == null ? "" : chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public String getTaxCode() {
		return taxCode == null ? "" : taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getCurrencyCode() {
		return currencyCode == null ? "" : currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getFundTransferCredit() {
		return fundTransferCredit == null ? "" : fundTransferCredit;
	}

	public void setFundTransferCredit(String fundTransferCredit) {
		this.fundTransferCredit = fundTransferCredit;
	}

	public String getFundTransferDebit() {
		return fundTransferDebit == null ? "" : fundTransferDebit;
	}

	public void setFundTransferDebit(String fundTransferDebit) {
		this.fundTransferDebit = fundTransferDebit;
	}

	public String getBillsPaymentCredit() {
		return billsPaymentCredit == null ? "" : billsPaymentCredit;
	}

	public void setBillsPaymentCredit(String billsPaymentCredit) {
		this.billsPaymentCredit = billsPaymentCredit;
	}

	public String getBillsPaymentDebit() {
		return billsPaymentDebit == null ? "" : billsPaymentDebit;
	}

	public void setBillsPaymentDebit(String billsPaymentDebit) {
		this.billsPaymentDebit = billsPaymentDebit;
	}

	public String getMobileRechargeCredit() {
		return mobileRechargeCredit == null ? "" : mobileRechargeCredit;
	}

	public void setMobileRechargeCredit(String mobileRechargeCredit) {
		this.mobileRechargeCredit = mobileRechargeCredit;
	}

	public String getMobileRechargeDebit() {
		return mobileRechargeDebit == null ? "" : mobileRechargeDebit;
	}

	public void setMobileRechargeDebit(String mobileRechargeDebit) {
		this.mobileRechargeDebit = mobileRechargeDebit;
	}

	public String getAuthenticatedUsername() {
		return authenticatedUsername == null ? "" : authenticatedUsername;
	}

	public void setAuthenticatedUsername(String authenticatedUsername) {
		this.authenticatedUsername = authenticatedUsername;
	}

	public String getAuthenticatedPassword() {
		return authenticatedPassword == null ? "" : authenticatedPassword;
	}

	public void setAuthenticatedPassword(String authenticatedPassword) {
		this.authenticatedPassword = authenticatedPassword;
	}

	public String getTransferLimitInternal() {
		return transferLimitInternal == null ? "" : transferLimitInternal;
	}

	public void setTransferLimitInternal(String transferLimitInternal) {
		this.transferLimitInternal = transferLimitInternal;
	}

	public String getTransferLimitExternal() {
		return transferLimitExternal == null ? "" : transferLimitExternal;
	}

	public void setTransferLimitExternal(String transferLimitExternal) {
		this.transferLimitExternal = transferLimitExternal;
	}

	public String getApplicationUsername() {
		return applicationUsername == null ? "" : applicationUsername;
	}

	public void setApplicationUsername(String applicationUsername) {
		this.applicationUsername = applicationUsername;
	}

	public String getChargeCodeBillsPayment() {
		return chargeCodeBillsPayment == null ? "" : chargeCodeBillsPayment;
	}

	public void setChargeCodeBillsPayment(String chargeCodeBillsPayment) {
		this.chargeCodeBillsPayment = chargeCodeBillsPayment;
	}

	public String getAppVersion() {
		return appVersion == null ? "" : appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getLincenceInfo() {
		return lincenceInfo == null ? "" : lincenceInfo;
	}

	public void setLincenceInfo(String lincenceInfo) {
		this.lincenceInfo = lincenceInfo;
	}

	public String getAlertPeriodInDays() {
		return alertPeriodInDays == null ? "0" : alertPeriodInDays;
	}

	public void setAlertPeriodInDays(String alertPeriodInDays) {
		this.alertPeriodInDays = alertPeriodInDays;
	}
	
	public String getGracePeriodInDays() {
		return gracePeriodInDays == null ? "0" : gracePeriodInDays;
	}

	public void setGracePeriodInDays(String gracePeriodInDays) {
		this.gracePeriodInDays = gracePeriodInDays;
	}

	public String getAuthenticateService() {
		return authenticateService == null ? "" : authenticateService;
	}

	public void setAuthenticateService(String authenticateService) {
		this.authenticateService = authenticateService;
	}

	public EmailAddress getMailRecipient() {
		return mailRecipient;
	}

	public void setMailRecipient(EmailAddress mailRecipient) {
		this.mailRecipient = mailRecipient;
	}
	
	
	
}
