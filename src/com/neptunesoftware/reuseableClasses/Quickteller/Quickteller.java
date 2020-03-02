package com.neptunesoftware.reuseableClasses.Quickteller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.CypherCrypt;
import com.neptunesoftware.reuseableClasses.ResponseConstants;
import com.neptunesoftware.reuseableClasses.ResponseModel;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.AccountReceivable;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.Beneficiary;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.FundTransferRequest;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.Initiation;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.Sender;
import com.neptunesoftware.reuseableClasses.Quickteller.FundTransfer.Termination;
import com.neptunesoftware.reuseableClasses.Quickteller.GetBankCode.BankResponse;
import com.neptunesoftware.reuseableClasses.Quickteller.NameEnquiry.NameEnquiryResponse;
import com.neptunesoftware.reuseableClasses.Quickteller.SendBillsPaymentAdvice.BillPaymentAdviceRequest;
import com.neptunesoftware.reuseableClasses.Quickteller.data.Errors;
import com.neptunesoftware.reuseableClasses.Quickteller.data.QuicktellerCredential;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;
import com.neptunesoftware.reuseableClasses.WebserviceCall.HttpResponse;
import com.neptunesoftware.reuseableClasses.WebserviceCall.WebserviceCall;


public class Quickteller {

	private QuicktellerConstants quicktellerConstants;
	private WebserviceCall webserviceCall;
	
	
	public Quickteller() {
		QuicktellerCredential quicktellerCredential = readConfig();
		
		if(quicktellerCredential.getResponseCode().equals(ResponseConstants.SUCCEESS_CODE))
			this.quicktellerConstants = new QuicktellerConstants()
											.baseUrl(quicktellerCredential.getBaseUrl())
											.clientId(quicktellerCredential.getClientId())
											.clientSecret(quicktellerCredential.getClientSecret())
											.transferCodePrefix(quicktellerCredential.getTransferCodePrefix())
											.terminalId(quicktellerCredential.getTerminalId())
											.initiatingEntityCode(quicktellerCredential.getInitiatingEntityCode())
											.signatureMethod(quicktellerCredential.getSignatureMethod());
		else
			this.quicktellerConstants = new QuicktellerConstants();
		
		this.webserviceCall = new WebserviceCall(quicktellerConstants.getBaseUrl());
	}
	
	public Quickteller(String baseUrl, String clientId, String clientSecret, String initiatingEntityCode,
    		String transferCodePrefix, String terminalId, String signatureMethod) {
		
		this.quicktellerConstants = new QuicktellerConstants(baseUrl, clientId, clientSecret, initiatingEntityCode,
	    													transferCodePrefix, terminalId, signatureMethod);
		this.webserviceCall = new WebserviceCall(quicktellerConstants.getBaseUrl());
	}
	
    public Quickteller baseUrl(String baseUrl) {
    	this.quicktellerConstants = quicktellerConstants.baseUrl(baseUrl);
    	return this;
    }
    
    public Quickteller clientId(String clientId) {
    	this.quicktellerConstants = quicktellerConstants.clientId(clientId);
    	return this;
    }
    
    public Quickteller clientSecret(String clientSecret) {
    	this.quicktellerConstants = quicktellerConstants.clientSecret(clientSecret);
    	return this;
    }
        
    public Quickteller initiatingEntityCode(String initiatingEntityCode) {
    	this.quicktellerConstants = quicktellerConstants.initiatingEntityCode(initiatingEntityCode);
    	return this;
    }
    
    public Quickteller transferCodePrefix(String transferCodePrefix) {
    	this.quicktellerConstants = quicktellerConstants.transferCodePrefix(transferCodePrefix);
    	return this;
    }
    
    public Quickteller terminalId(String terminalId) {
    	this.quicktellerConstants = quicktellerConstants.terminalId(terminalId);
    	return this;
    }
    
    public Quickteller signatureMethod(String signatureMethod) {
    	this.quicktellerConstants = quicktellerConstants.signatureMethod(signatureMethod);
    	return this;
    }
    
	
    public String getClientId() {
    	return this.quicktellerConstants.getBaseUrl();
    }
	
	public static void main(String[] args) {
		
//		Quickteller quickteller = new Quickteller().baseUrl("hello").clientId("hi").clientSecret("there");
//		
//		System.out.println("ClientId: " + quickteller.getClientId());
		
//		Quickteller quickteller = new Quickteller();
//		//System.out.println("getBanks: \n\r" + quickteller.getBankCodes());
//		System.out.println("name: \n\r" + quickteller.nameEnquiry("063", "0034819411"));
		
		
		System.out.println("RubikonConfig: \n\r" + CommonMethods.ObjectToJsonString(Quickteller.readRubikonConfig()));
	}
	
	
	
	
	
	
	
	
	//******* GET methods *********
	
	public String getBankCodes(){
		
		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.GET_BANK_CODE_URL, QuicktellerConstants.GET);
		
		HttpResponse httpResponse = webserviceCall.getMethod(QuicktellerConstants.GET_BANK_CODE_URL, extraHeaders);

		String resp = removeLocalBank(httpResponse.getResponseBody());
		return resp;
	}

	public String getBillers(){
		
		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.GET_BILLER_URL, QuicktellerConstants.GET);

		HttpResponse httpResponse = webserviceCall.getMethod(QuicktellerConstants.GET_BILLER_URL, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}

	public String getBillerCategories() {

		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.GET_BILLER_CATEGORIES_URL, QuicktellerConstants.GET);

		HttpResponse httpResponse = webserviceCall.getMethod(QuicktellerConstants.GET_BILLER_CATEGORIES_URL, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}

	public String getBillersByCategory(String id) {
		
		String URL = QuicktellerConstants.GET_BILLER_BY_CATEGORY_URL_PREFIX + id + QuicktellerConstants.GET_BILLER_BY_CATEGORY_URL_SUFFIX;

		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(URL, QuicktellerConstants.GET);

		HttpResponse httpResponse = webserviceCall.getMethod(URL, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}

	public String getBillersPaymentItems(String billerId) {
		
		String URL = QuicktellerConstants.GET_BILLER_PAYMENT_ITEMS_URL_PREFIX + billerId
				+ QuicktellerConstants.GET_BILLER_PAYMENT_ITEMS_URL_SUFFIX;

		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(URL, QuicktellerConstants.GET);

		HttpResponse httpResponse = webserviceCall.getMethod(URL, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}

	public String queryTransaction(String requestreference) {
		// WebserviceCall webserviceCall = new WebserviceCall();

		String URL = QuicktellerConstants.QUERY_TRANSACTION_URL + requestreference;

		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(URL, QuicktellerConstants.GET);

		HttpResponse httpResponse = new HttpResponse();
		try {
			httpResponse = webserviceCall.getMethod(URL, extraHeaders);
		} catch (Exception e) {}

		String resp = httpResponse.getResponseBody() == null ? "" : httpResponse.getResponseBody();
		return resp;
	}
	
	public String nameEnquiry(String bankCode, String accountNo) {
		
		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.NAME_ENQUIRY_URL, QuicktellerConstants.GET);
		extraHeaders.put("bankCode", bankCode);
		extraHeaders.put("accountId", accountNo);

		HttpResponse httpResponse = webserviceCall.getMethod(QuicktellerConstants.NAME_ENQUIRY_URL, extraHeaders);

		String resp = customizeResponse(httpResponse.getResponseBody());
		return resp;
	}

	
	
	
	//******* POST methods *********	
	
	public String fundTransfer(String beneficiaryAcctNumber, String beneficiaryName, String amountInKobo, 
			String beneficiaryBankCode, String senderName) {
		
		FundTransferRequest fundTransferRequest = createFundTransferRequest(beneficiaryAcctNumber, 
				beneficiaryName, amountInKobo, beneficiaryBankCode, senderName);
		
		String fundTransferRequestStr = CommonMethods.ObjectToJsonString(fundTransferRequest);

		HttpResponse httpResponse = fundTransferService(fundTransferRequestStr);
		
		if (httpResponse == null || httpResponse.getStatusCode() == -1)
			return "";
		
		return httpResponse.getResponseBody();
		
	}
	
	public String sendBillPaymentAdvice(String paymentCode, String customerId, String customerMobile, 
			String customerEmail, String amount, AtomicReference<String> requestReference) {
		
		BillPaymentAdviceRequest billPaymentAdviceRequest = createBillPaymentAdviceRequest(paymentCode, customerId, 
				customerMobile, customerEmail, amount);
		
		// used as a reference variable so the value of requestReference can be accessed outside this class
		requestReference.set(billPaymentAdviceRequest.requestReference);
		
		String billPaymentAdviceRequestStr = CommonMethods.ObjectToJsonString(billPaymentAdviceRequest);
		
		HttpResponse httpResponse = sendBillPaymentAdviceInterswitch(billPaymentAdviceRequestStr);
		
		if (httpResponse == null || httpResponse.getStatusCode() == -1)
			return "";
		
		return httpResponse.getResponseBody();
		
	}
	
	public String customerValidation(String body) {
		// WebserviceCall webserviceCall = new WebserviceCall();

		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.CUSTOMER_VALIDATION_URL, QuicktellerConstants.POST);

		HttpResponse httpResponse = webserviceCall.postMethod(QuicktellerConstants.CUSTOMER_VALIDATION_URL, body, extraHeaders);

		String resp = httpResponse.getResponseBody();
		return resp;
	}
	
	
	
	
	
	//****** Used by getBankCodes *******
	private String removeLocalBank(String responseBody){
		
		// if response is empty, return
		if (responseBody.isEmpty() && responseBody.equals(null))
			return CommonMethods.ObjectToJsonString(new BankResponse(ResponseConstants.WEBSERVICE_UNAVAILABLE_CODE, ResponseConstants.WEBSERVICE_UNAVAILABLE_MESSAGE));
		
		//if response is an error instance, return
		Errors errorResp = (Errors) CommonMethods.JSONStringToObject(responseBody, Errors.class);
		if(!errorResp.error.getCode().isEmpty()) {
			return customizeErrorResponse(errorResp);
		}
				
		BankResponse bankResponse = new BankResponse();
		
		try {
			bankResponse = (BankResponse) CommonMethods.JSONStringToObject(responseBody, BankResponse.class);
						
		} catch (Exception e) {
			String message = ResponseConstants.EXCEPTION_MESSAGE + "- " + e.getMessage();
			return CommonMethods.ObjectToJsonString(new BankResponse(ResponseConstants.EXCEPTION_CODE, message));
		}
		
		//remove from the list of banks Amju's cbn code
		bankResponse.getBanks().removeIf(bank -> bank.getCbnCode().equals("306"));
		bankResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		bankResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		
		return CommonMethods.ObjectToJsonString(bankResponse);
	}
	
	//****** Used by nameEnquiry *******
	private String customizeResponse(String responseBody){
		
		// if response is empty, return
		if (responseBody.isEmpty() && responseBody.equals(null))
			return CommonMethods.ObjectToJsonString(new NameEnquiryResponse(ResponseConstants.WEBSERVICE_UNAVAILABLE_CODE, ResponseConstants.WEBSERVICE_UNAVAILABLE_MESSAGE));
		
		//if response is an error instance, return
		Errors errorResp = (Errors) CommonMethods.JSONStringToObject(responseBody, Errors.class);
		if(!errorResp.error.getCode().isEmpty()) {
			return customizeErrorResponse(errorResp);
		}
		
		NameEnquiryResponse nameEnquiryResponse = new NameEnquiryResponse();
		
		try {
			nameEnquiryResponse = (NameEnquiryResponse) CommonMethods.JSONStringToObject(responseBody, NameEnquiryResponse.class);
		} catch (Exception e) {
			String message = ResponseConstants.EXCEPTION_MESSAGE + "- " + e.getMessage();
			return CommonMethods.ObjectToJsonString(new NameEnquiryResponse(ResponseConstants.EXCEPTION_CODE, message));
		}
		
		nameEnquiryResponse.setResponseCode(ResponseConstants.SUCCEESS_CODE);
		nameEnquiryResponse.setResponseMessage(ResponseConstants.SUCCEESS_MESSAGE);
		
		return CommonMethods.ObjectToJsonString(nameEnquiryResponse);
	}
	
	private String customizeErrorResponse(Errors errorResp){	
		ResponseModel responseModel = new ResponseModel();
		responseModel.setResponseCode(ResponseConstants.WEBSERVICE_FAILED_RESPONSE_CODE);
		responseModel.setResponseMessage(CommonMethods.ObjectToJsonString(errorResp.error));
		
		return CommonMethods.ObjectToJsonString(responseModel);
	}
	
	
	
	
	//*** used by fundTransfer ****
	private FundTransferRequest createFundTransferRequest(String beneficiaryAcctNumber,
			String beneficiaryName, String amount, String beneficiaryBankCode, String senderName) {
		FundTransferRequest fundTransfer = null;

		try {
		
			fundTransfer = new FundTransferRequest();

			Beneficiary beneficiary = new Beneficiary("", "", beneficiaryName, beneficiaryName);
			Initiation initiation = new Initiation(amount, QuicktellerConstants.CURRENCY_CODE_NUMBER,
					QuicktellerConstants.INITIATING_PAYMENT_METHOD_CODE, QuicktellerConstants.CHANNEL_LOCATION);
			Sender sender = new Sender("", "", senderName, senderName);

			AccountReceivable accountReceivable = new AccountReceivable(beneficiaryAcctNumber,
					QuicktellerConstants.ACCOUNT_TYPE_DEFAULT);
			Termination termination = new Termination(amount, beneficiaryBankCode, QuicktellerConstants.CURRENCY_CODE_NUMBER,
					QuicktellerConstants.TERMINATING_PAYMENT_METHOD_CODE, QuicktellerConstants.COUNTRY_CODE);
			termination.setAccountReceivable(accountReceivable);

			// set the MAC value for the request object
			String macCipher = "" + initiation.getAmount() + initiation.getCurrencyCode()
					+ initiation.getPaymentMethodCode() + termination.getTerminationAmount()
					+ termination.getTerminationCurrencyCode() + termination.getTerminationPaymentMethodCode()
					+ termination.getTerminationCountryCode();
			String MAC = QuicktellerConstants.SHA512(macCipher);

			fundTransfer.mac = MAC;
			fundTransfer.beneficiary = beneficiary;
			fundTransfer.initiatingEntityCode = quicktellerConstants.getInitiatingEntityCode();
			fundTransfer.initiation = initiation;
			fundTransfer.sender = sender;
			fundTransfer.termination = termination;
			fundTransfer.transferCode = quicktellerConstants.getTransferCodePrefix() + QuicktellerConstants.timeStamp();

			return fundTransfer;
			

		} catch (Exception e) {
			System.out.println("Service Endpoint Unavailable.");
			return fundTransfer;
		}
	}
	
	private HttpResponse fundTransferService(String body) {
		
		HttpResponse httpResponse = new HttpResponse();
		try {
			FundTransferRequest fundTransferReq = (FundTransferRequest) CommonMethods.JSONStringToObject(body, FundTransferRequest.class);

			// set the MAC value for the request object
			fundTransferReq.mac = generateMAC(fundTransferReq);

			// create a json string from the request object
			String request = CommonMethods.ObjectToJSONOrXMLstring(body, fundTransferReq);

			HashMap<String, String> extraHeaders = new HashMap<String, String>();
			extraHeaders = commonHeaders(QuicktellerConstants.FUNDS_TRANSFER_URL, QuicktellerConstants.POST);

			httpResponse = webserviceCall.postMethod(QuicktellerConstants.FUNDS_TRANSFER_URL, request, extraHeaders);

			// String resp = httpResponse.getResponseBody();
			return httpResponse;

		} catch (Exception ex) {
			return null;
		}

	}
	
	private String generateMAC(FundTransferRequest fundTransfer){
		try {
			// collect the iniatiation object
			Initiation initiation = new Initiation();
			initiation = fundTransfer.initiation;

			// collect the termination object
			Termination termination = new Termination();
			termination = fundTransfer.termination;

			// compute the MAC cipher
			String macCipher = "" + initiation.getAmount() + initiation.getCurrencyCode()
					+ initiation.getPaymentMethodCode() + termination.getTerminationAmount()
					+ termination.getTerminationCurrencyCode() + termination.getTerminationPaymentMethodCode()
					+ termination.getTerminationCountryCode();

			// encode MAC cipher
			return QuicktellerConstants.SHA512(macCipher);

		} catch (Exception ex) {
			return "";
		}
	}
		
	
	//*** used by sendBillPaymentAdvice ****
	public BillPaymentAdviceRequest createBillPaymentAdviceRequest(String paymentCode,
			String customerId, String customerMobile, String customerEmail, String amount) {
		
		BillPaymentAdviceRequest BPA = null;

		try {
			BPA = new BillPaymentAdviceRequest();

			BPA.terminalId = quicktellerConstants.getTerminalId();
			BPA.paymentCode = paymentCode;
			BPA.customerId = customerId;
			BPA.customerMobile = customerMobile;
			BPA.customerEmail = customerEmail;
			BPA.amount = amount;
			BPA.requestReference = quicktellerConstants.getTransferCodePrefix() + QuicktellerConstants.timeStamp().substring(2);

			return BPA;

		} catch (Exception e) {
			System.out.println("Service Endpoint Unavailable.");
			return BPA;
		}
	}
	
	public HttpResponse sendBillPaymentAdviceInterswitch(String request) {
		//This method is used by BillspAyment and AirtimeRecharge
		
		HashMap<String, String> extraHeaders = new HashMap<String, String>();
		extraHeaders = commonHeaders(QuicktellerConstants.SEND_BILL_PAYMENT_ADVICE_URL, QuicktellerConstants.POST);

		HttpResponse httpResponse = webserviceCall.postMethod(QuicktellerConstants.SEND_BILL_PAYMENT_ADVICE_URL, request, extraHeaders);

		//String resp = httpResponse.getResponseBody();
		return httpResponse;
	}

	
	//*** used by all GET and POST methods
	private HashMap<String, String> commonHeaders(String path, String httpMethod){
		
		String url = quicktellerConstants.getBaseUrl() + path;
		
		String encodedResourceUrl = "";
		try {
			encodedResourceUrl = URLEncoder.encode(url, "ISO-8859-1"); // new String(url.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {}
		
		String timestamp = QuicktellerConstants.timeStamp();
		String nonce = QuicktellerConstants.nonce();
		
		String signatureCipher = httpMethod + "&" + encodedResourceUrl + "&" + timestamp + "&" + nonce + "&" 
								+ quicktellerConstants.getClientId() + "&" + quicktellerConstants.getClientSecret();
		
		String signature = QuicktellerConstants.signature(signatureCipher, quicktellerConstants.getSignatureMethod());
		
		
		HashMap<String, String> commonHeaders = new HashMap<String, String>();
		
		commonHeaders.put("TerminalId", quicktellerConstants.getTerminalId());
				
		commonHeaders.put("Content-Type", QuicktellerConstants.CONTENT_TYPE);
		
		commonHeaders.put("Authorization", quicktellerConstants.getAuthorization());
		
		commonHeaders.put("Timestamp", timestamp);

		commonHeaders.put("Nonce", nonce);

		commonHeaders.put("SignatureMethod", quicktellerConstants.getSignatureMethod());

		commonHeaders.put("Signature", signature);
		
		return commonHeaders;
	}
	
	
	//*** used by constructor
	public static QuicktellerCredential readConfig() {
		
		QuicktellerCredential quicktellerCredential = new QuicktellerCredential();
		
		try {
		String content = CommonMethods.getInfo("QuicktellerInfo.xml", Quickteller.class);
		
		quicktellerCredential = CommonMethods.xmlStringToObject(content, QuicktellerCredential.class);
		quicktellerCredential = decryptContent(quicktellerCredential);
		
		quicktellerCredential.setResponseCode(ResponseConstants.SUCCEESS_CODE);
				
		} catch(Exception e) {
			System.out.println("Cannot read QuicktellerInfo.xml");
			quicktellerCredential.setResponseCode(ResponseConstants.FILE_ERROR_CODE);
		}
		
		return quicktellerCredential;
	}
	
	private static QuicktellerCredential decryptContent(QuicktellerCredential quicktellerCredential) {
		QuicktellerCredential quicktellerCred = quicktellerCredential;
		try {
			String baseUrl = CypherCrypt.deCypher(quicktellerCredential.getBaseUrl().trim()) == null || CypherCrypt.deCypher(quicktellerCredential.getBaseUrl().trim()).equals("")
					? quicktellerCredential.getBaseUrl().trim() : CypherCrypt.deCypher(quicktellerCredential.getBaseUrl().trim());
					
			String clientId = CypherCrypt.deCypher(quicktellerCredential.getClientId().trim()) == null || CypherCrypt.deCypher(quicktellerCredential.getClientId().trim()).equals("")
					? quicktellerCredential.getClientId().trim() : CypherCrypt.deCypher(quicktellerCredential.getClientId().trim());
					
			String clientSecret = CypherCrypt.deCypher(quicktellerCredential.getClientSecret().trim()) == null || CypherCrypt.deCypher(quicktellerCredential.getClientSecret().trim()).equals("")
					? quicktellerCredential.getClientSecret().trim() : CypherCrypt.deCypher(quicktellerCredential.getClientSecret().trim());
					
			String initiatingEntityCode = CypherCrypt.deCypher(quicktellerCredential.getInitiatingEntityCode().trim()) == null || CypherCrypt.deCypher(quicktellerCredential.getInitiatingEntityCode().trim()).equals("")
					? quicktellerCredential.getInitiatingEntityCode().trim() : CypherCrypt.deCypher(quicktellerCredential.getInitiatingEntityCode().trim());
					
			String signatureMethod = CypherCrypt.deCypher(quicktellerCredential.getSignatureMethod().trim()) == null || CypherCrypt.deCypher(quicktellerCredential.getSignatureMethod().trim()).equals("")
					? quicktellerCredential.getSignatureMethod().trim() : CypherCrypt.deCypher(quicktellerCredential.getSignatureMethod().trim());
					
			String terminalId = CypherCrypt.deCypher(quicktellerCredential.getTerminalId().trim()) == null || CypherCrypt.deCypher(quicktellerCredential.getTerminalId().trim()).equals("")
					? quicktellerCredential.getTerminalId().trim() : CypherCrypt.deCypher(quicktellerCredential.getTerminalId().trim());
					
			String transferCodePrefix = CypherCrypt.deCypher(quicktellerCredential.getTransferCodePrefix().trim()) == null || CypherCrypt.deCypher(quicktellerCredential.getTransferCodePrefix().trim()).equals("")
					? quicktellerCredential.getTransferCodePrefix().trim() : CypherCrypt.deCypher(quicktellerCredential.getTransferCodePrefix().trim());
			
			quicktellerCredential.setBaseUrl(baseUrl);
			quicktellerCredential.setClientId(clientId);
			quicktellerCredential.setClientSecret(clientSecret);
			quicktellerCredential.setInitiatingEntityCode(initiatingEntityCode);
			quicktellerCredential.setSignatureMethod(signatureMethod);
			quicktellerCredential.setTerminalId(terminalId);
			quicktellerCredential.setTransferCodePrefix(transferCodePrefix);
			
			return quicktellerCredential;
			
		} catch (Exception e) {
			System.out.println("QuicktellerCredential: \n" + CommonMethods.objectToXml(quicktellerCredential));
			System.out.println("Cannot decrypt content");
			
			return quicktellerCred;
		}
		
	}
	
	
	public static RubikonCredential readRubikonConfig() {
		
		RubikonCredential rubikonCredential = new RubikonCredential();
		
		try {
		String content = CommonMethods.getInfo("RubikonInfo.xml", Quickteller.class);
				
		rubikonCredential = CommonMethods.xmlStringToObject(content, RubikonCredential.class);
		rubikonCredential = decryptContent(rubikonCredential);
		
		rubikonCredential.setResponseCode(ResponseConstants.SUCCEESS_CODE);
				
		} catch(Exception e) {
			System.out.println("Cannot read RubikonInfo.xml");
			rubikonCredential.setResponseCode(ResponseConstants.FILE_ERROR_CODE);
		}
		
		return rubikonCredential;
	}
	
	private static RubikonCredential decryptContent(RubikonCredential rubikonCredential) {
		RubikonCredential rubikonCred = rubikonCredential;
		try {
			String ipAddress = CypherCrypt.deCypher(rubikonCredential.getIpAddress().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getIpAddress().trim()).equals("")
					? rubikonCredential.getIpAddress().trim() : CypherCrypt.deCypher(rubikonCredential.getIpAddress().trim());
					
			String portNumber = CypherCrypt.deCypher(rubikonCredential.getPortNumber().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getPortNumber().trim()).equals("")
					? rubikonCredential.getPortNumber().trim() : CypherCrypt.deCypher(rubikonCredential.getPortNumber().trim());
					
			String channelId = CypherCrypt.deCypher(rubikonCredential.getChannelId().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getChannelId().trim()).equals("")
					? rubikonCredential.getChannelId().trim() : CypherCrypt.deCypher(rubikonCredential.getChannelId().trim());
					
			String channelCode = CypherCrypt.deCypher(rubikonCredential.getChannelCode().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getChannelCode().trim()).equals("")
					? rubikonCredential.getChannelCode().trim() : CypherCrypt.deCypher(rubikonCredential.getChannelCode().trim());
					
			String transactionFee = CypherCrypt.deCypher(rubikonCredential.getTransactionFee().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getTransactionFee().trim()).equals("")
					? rubikonCredential.getTransactionFee().trim() : CypherCrypt.deCypher(rubikonCredential.getTransactionFee().trim());
					
			String chargeCode = CypherCrypt.deCypher(rubikonCredential.getChargeCode().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getChargeCode().trim()).equals("")
					? rubikonCredential.getChargeCode().trim() : CypherCrypt.deCypher(rubikonCredential.getChargeCode().trim());
					
			String taxCode = CypherCrypt.deCypher(rubikonCredential.getTaxCode().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getTaxCode().trim()).equals("")
					? rubikonCredential.getTaxCode().trim() : CypherCrypt.deCypher(rubikonCredential.getTaxCode().trim());
					
			String currencyCode = CypherCrypt.deCypher(rubikonCredential.getCurrencyCode().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getCurrencyCode().trim()).equals("")
					? rubikonCredential.getCurrencyCode().trim() : CypherCrypt.deCypher(rubikonCredential.getCurrencyCode().trim());
					
			String fundTransferCredit = CypherCrypt.deCypher(rubikonCredential.getFundTransferCredit().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getFundTransferCredit().trim()).equals("")
					? rubikonCredential.getFundTransferCredit().trim() : CypherCrypt.deCypher(rubikonCredential.getFundTransferCredit().trim());
					
			String fundTransferDebit = CypherCrypt.deCypher(rubikonCredential.getFundTransferDebit().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getFundTransferDebit().trim()).equals("")
					? rubikonCredential.getFundTransferDebit().trim() : CypherCrypt.deCypher(rubikonCredential.getFundTransferDebit().trim());
					
			String billsPaymentCredit = CypherCrypt.deCypher(rubikonCredential.getBillsPaymentCredit().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getBillsPaymentCredit().trim()).equals("")
					? rubikonCredential.getBillsPaymentCredit().trim() : CypherCrypt.deCypher(rubikonCredential.getBillsPaymentCredit().trim());
					
			String billsPaymentDebit= CypherCrypt.deCypher(rubikonCredential.getBillsPaymentDebit().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getBillsPaymentDebit().trim()).equals("")
					? rubikonCredential.getBillsPaymentDebit().trim() : CypherCrypt.deCypher(rubikonCredential.getBillsPaymentDebit().trim());
					
			String mobileRechargeCredit = CypherCrypt.deCypher(rubikonCredential.getMobileRechargeCredit().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getMobileRechargeCredit().trim()).equals("")
					? rubikonCredential.getMobileRechargeCredit().trim() : CypherCrypt.deCypher(rubikonCredential.getMobileRechargeCredit().trim());
					
			String mobileRechargeDebit = CypherCrypt.deCypher(rubikonCredential.getMobileRechargeDebit().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getMobileRechargeDebit().trim()).equals("")
					? rubikonCredential.getMobileRechargeDebit().trim() : CypherCrypt.deCypher(rubikonCredential.getMobileRechargeDebit().trim());
					
			String authenticationUsername = CypherCrypt.deCypher(rubikonCredential.getAuthenticatedUsername().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getAuthenticatedUsername().trim()).equals("")
					? rubikonCredential.getAuthenticatedUsername().trim() : CypherCrypt.deCypher(rubikonCredential.getAuthenticatedUsername().trim());
					
			String authenticationPassword = CypherCrypt.deCypher(rubikonCredential.getAuthenticatedPassword().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getAuthenticatedPassword().trim()).equals("")
					? rubikonCredential.getAuthenticatedPassword().trim() : CypherCrypt.deCypher(rubikonCredential.getAuthenticatedPassword().trim());
					
			String transferLimitInternal = CypherCrypt.deCypher(rubikonCredential.getTransferLimitInternal().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getTransferLimitInternal().trim()).equals("")
					? rubikonCredential.getTransferLimitInternal().trim() : CypherCrypt.deCypher(rubikonCredential.getTransferLimitInternal().trim());
					
			String transferLimitExternal = CypherCrypt.deCypher(rubikonCredential.getTransferLimitExternal().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getTransferLimitExternal().trim()).equals("")
					? rubikonCredential.getTransferLimitExternal().trim() : CypherCrypt.deCypher(rubikonCredential.getTransferLimitExternal().trim());
					
			String applicationUsername = CypherCrypt.deCypher(rubikonCredential.getApplicationUsername().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getApplicationUsername().trim()).equals("")
					? rubikonCredential.getApplicationUsername().trim() : CypherCrypt.deCypher(rubikonCredential.getApplicationUsername().trim());
			
			String chargeCodeBillsPayment = CypherCrypt.deCypher(rubikonCredential.getChargeCodeBillsPayment().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getChargeCodeBillsPayment().trim()).equals("")
					? rubikonCredential.getChargeCodeBillsPayment().trim() : CypherCrypt.deCypher(rubikonCredential.getChargeCodeBillsPayment().trim());
			
			String authenticateService = CypherCrypt.deCypher(rubikonCredential.getAuthenticateService().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getAuthenticateService().trim()).equals("")
					? rubikonCredential.getAuthenticateService().trim() : CypherCrypt.deCypher(rubikonCredential.getAppVersion().trim());
							
			String appVersion = CypherCrypt.deCypher(rubikonCredential.getAppVersion().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getAppVersion().trim()).equals("")
					? rubikonCredential.getAppVersion().trim() : CypherCrypt.deCypher(rubikonCredential.getAppVersion().trim());
					
			String lincenceInfo = CypherCrypt.deCypher(rubikonCredential.getLincenceInfo().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getLincenceInfo().trim()).equals("")
					? rubikonCredential.getLincenceInfo().trim() : CypherCrypt.deCypher(rubikonCredential.getLincenceInfo().trim());
			
			String alertPeriodInDays = CypherCrypt.deCypher(rubikonCredential.getAlertPeriodInDays().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getAlertPeriodInDays().trim()).equals("")
					? rubikonCredential.getAlertPeriodInDays().trim() : CypherCrypt.deCypher(rubikonCredential.getAlertPeriodInDays().trim());
							
			String gracePeriodInDays = CypherCrypt.deCypher(rubikonCredential.getGracePeriodInDays().trim()) == null || CypherCrypt.deCypher(rubikonCredential.getGracePeriodInDays().trim()).equals("")
					? rubikonCredential.getGracePeriodInDays().trim() : CypherCrypt.deCypher(rubikonCredential.getGracePeriodInDays().trim());
			
			
			// collect all mail recipients
			List<String> mailRecipients = rubikonCredential.getMailRecipient().getEmailAddress();
						
			// go through each mail recipients
			int index = 0;
			for (String mailAddress : mailRecipients) {
				// and decrypt value passed if it was encrypted
				String email = CypherCrypt.deCypher(mailAddress.trim()) == null || CypherCrypt.deCypher(mailAddress.trim()).equals("")
						? mailAddress.trim() : CypherCrypt.deCypher(mailAddress.trim());
				
				// set the values back to the object
				mailRecipients.set(index++, email);
			}
			
			
			rubikonCredential.setIpAddress(ipAddress);
			rubikonCredential.setPortNumber(portNumber);
			rubikonCredential.setChannelId(channelId);
			rubikonCredential.setChannelCode(channelCode);
			rubikonCredential.setTransactionFee(transactionFee);
			rubikonCredential.setChargeCode(chargeCode);
			rubikonCredential.setTaxCode(taxCode);
			rubikonCredential.setCurrencyCode(currencyCode);
			rubikonCredential.setFundTransferCredit(fundTransferCredit);
			rubikonCredential.setFundTransferDebit(fundTransferDebit);
			rubikonCredential.setBillsPaymentCredit(billsPaymentCredit);
			rubikonCredential.setBillsPaymentDebit(billsPaymentDebit);
			rubikonCredential.setMobileRechargeCredit(mobileRechargeCredit);
			rubikonCredential.setMobileRechargeDebit(mobileRechargeDebit);
			rubikonCredential.setAuthenticatedUsername(authenticationUsername);
			rubikonCredential.setAuthenticatedPassword(authenticationPassword);
			rubikonCredential.setTransferLimitInternal(transferLimitInternal);
			rubikonCredential.setTransferLimitExternal(transferLimitExternal);
			rubikonCredential.setApplicationUsername(applicationUsername);
			rubikonCredential.setChargeCodeBillsPayment(chargeCodeBillsPayment);
			rubikonCredential.setAuthenticateService(authenticateService);
			rubikonCredential.setAppVersion(appVersion);
			rubikonCredential.setLincenceInfo(lincenceInfo);
			rubikonCredential.setAlertPeriodInDays(alertPeriodInDays);
			rubikonCredential.setGracePeriodInDays(gracePeriodInDays);
			rubikonCredential.getMailRecipient().setEmailAddress(mailRecipients);
			
			return rubikonCredential;
			
		} catch (Exception e) {
			System.out.println("RubikonCredential: \n" + CommonMethods.objectToXml(rubikonCredential));
			System.out.println("Cannot decrypt content");
			
			return rubikonCred;
		}		
		
	}
	
	

	
	
}
