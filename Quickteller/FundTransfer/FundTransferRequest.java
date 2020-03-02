package com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer;

public class FundTransferRequest {
	public String mac = "";
	public Beneficiary beneficiary = new Beneficiary("","","","");
	public String initiatingEntityCode = "";
	public Initiation initiation = new Initiation("","","","");
	public Sender sender = new Sender("","","","");
	public Termination termination = new Termination("","","","","");
	public String transferCode = "";	
}