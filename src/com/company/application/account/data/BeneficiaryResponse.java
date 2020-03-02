package com.company.application.account.data;

import java.util.List;

import com.neptunesoftware.reuseableClasses.ResponseModel;

public class BeneficiaryResponse extends ResponseModel{

	private List<Beneficiary> beneficiary;

	public BeneficiaryResponse() {
		super();
	}
	
	public BeneficiaryResponse(String responseCode, String responseMessage) {
		super(responseCode, responseMessage);
	}
	
	public BeneficiaryResponse(List<Beneficiary> beneficiary) {
		super();
		this.beneficiary = beneficiary;
	}

	public List<Beneficiary> getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(List<Beneficiary> beneficiary) {
		this.beneficiary = beneficiary;
	}
	
}
