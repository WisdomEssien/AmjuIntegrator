package com.company.application.account.data;

public class BeneficiaryRequest extends Beneficiary {

	private String moduleId;
	private String senderAcctNo;
	
	public BeneficiaryRequest() {super();}
	
	public BeneficiaryRequest(String beneficiaryId, String beneficiaryAcctNo, String beneficiaryAcctName, String bankCode,
			String bankName, String moduleId, String senderAcctNo) {
		
		super(beneficiaryId, beneficiaryAcctNo, beneficiaryAcctName, bankCode, bankName);
		this.moduleId = moduleId;
		this.senderAcctNo = senderAcctNo;
	}
	
	public String getModuleId() {
		return moduleId;
	}
	
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	public String getSenderAcctNo() {
		return senderAcctNo;
	}
	
	public void setSenderAcctNo(String senderAcctNo) {
		this.senderAcctNo = senderAcctNo;
	}
	
}
