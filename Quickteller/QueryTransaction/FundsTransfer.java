package com.neptunesoftware.reuseableClasses.Quickteller.QueryTransaction;

public class FundsTransfer {

	private String beneficiaryName;
	private String beneficiaryEmail;
	private String beneficiaryPhone;
	private String terminatingEntityName;
	private String terminatingAccountNumber;
	private String terminatingAccountType;
	
	public FundsTransfer() {}
	
	public FundsTransfer(String beneficiaryName, String beneficiaryEmail, String beneficiaryPhone,
			String terminatingEntityName, String terminatingAccountNumber, String terminatingAccountType) {
		super();
		this.beneficiaryName = beneficiaryName;
		this.beneficiaryEmail = beneficiaryEmail;
		this.beneficiaryPhone = beneficiaryPhone;
		this.terminatingEntityName = terminatingEntityName;
		this.terminatingAccountNumber = terminatingAccountNumber;
		this.terminatingAccountType = terminatingAccountType;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getBeneficiaryEmail() {
		return beneficiaryEmail;
	}

	public void setBeneficiaryEmail(String beneficiaryEmail) {
		this.beneficiaryEmail = beneficiaryEmail;
	}

	public String getBeneficiaryPhone() {
		return beneficiaryPhone;
	}

	public void setBeneficiaryPhone(String beneficiaryPhone) {
		this.beneficiaryPhone = beneficiaryPhone;
	}

	public String getTerminatingEntityName() {
		return terminatingEntityName;
	}

	public void setTerminatingEntityName(String terminatingEntityName) {
		this.terminatingEntityName = terminatingEntityName;
	}

	public String getTerminatingAccountNumber() {
		return terminatingAccountNumber;
	}

	public void setTerminatingAccountNumber(String terminatingAccountNumber) {
		this.terminatingAccountNumber = terminatingAccountNumber;
	}

	public String getTerminatingAccountType() {
		return terminatingAccountType;
	}

	public void setTerminatingAccountType(String terminatingAccountType) {
		this.terminatingAccountType = terminatingAccountType;
	}
	
	
	
}
