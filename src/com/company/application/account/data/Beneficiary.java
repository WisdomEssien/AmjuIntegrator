package com.company.application.account.data;

public class Beneficiary {
	
	private String beneficiaryId;
	private String beneficiaryAcctNo;
	private String beneficiaryAcctName;
	private String bankCode;
	private String bankName;
	
	public Beneficiary(String beneficiaryId, String beneficiaryAcctNo, String beneficiaryAcctName, String bankCode,
			String bankName) {
		
		this.beneficiaryId = beneficiaryId;
		this.beneficiaryAcctNo = beneficiaryAcctNo;
		this.beneficiaryAcctName = beneficiaryAcctName;
		this.bankCode = bankCode;
		this.bankName = bankName;
	}
	
	public Beneficiary() {		
	}

	public String getBeneficiaryId() {
		return beneficiaryId;
	}

	public void setBeneficiaryId(String beneficiaryId) {
		this.beneficiaryId = beneficiaryId;
	}

	public String getBeneficiaryAcctNo() {
		return beneficiaryAcctNo;
	}

	public void setBeneficiaryAcctNo(String beneficiaryAcctNo) {
		this.beneficiaryAcctNo = beneficiaryAcctNo;
	}

	public String getBeneficiaryAcctName() {
		return beneficiaryAcctName;
	}

	public void setBeneficiaryAcctName(String beneficiaryAcctName) {
		this.beneficiaryAcctName = beneficiaryAcctName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	
}
