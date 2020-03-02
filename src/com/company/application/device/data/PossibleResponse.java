package com.company.application.device.data;

import java.util.List;

public class PossibleResponse {

	List<Response> responses;
	
	public PossibleResponse() {
	}

	public PossibleResponse(List<Response> responses) {
		super();
		this.responses = responses;
	}

	public List<Response> getResponses() {
		return responses;
	}

	public void setResponses(List<Response> responses) {
		this.responses = responses;
	}
	
	
}
