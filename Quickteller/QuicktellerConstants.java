package com.neptunesoftware.reuseableClasses.Quickteller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import org.bouncycastle.util.encoders.Base64;

public class QuicktellerConstants {
    
    //service URIs
    public final static String GET_BANK_CODE_URL = "api/v2/quickteller/configuration/fundstransferbanks";
    public final static String GET_BILLER_URL = "api/v2/quickteller/billers";
    public final static String GET_BILLER_CATEGORIES_URL = "api/v2/quickteller/categorys";
    public final static String GET_BILLER_BY_CATEGORY_URL_PREFIX = "api/v2/quickteller/categorys/";
    public final static String GET_BILLER_BY_CATEGORY_URL_SUFFIX = "/billers";
    public final static String GET_BILLER_PAYMENT_ITEMS_URL_PREFIX = "api/v2/quickteller/billers/";
    public final static String GET_BILLER_PAYMENT_ITEMS_URL_SUFFIX = "/paymentitems";
    public final static String SEND_BILL_PAYMENT_ADVICE_URL = "api/v2/quickteller/payments/advices";
    public final static String NAME_ENQUIRY_URL = "api/v1/nameenquiry/banks/accounts/names";
    public final static String FUNDS_TRANSFER_URL = "api/v2/quickteller/payments/transfers";
    public final static String QUERY_TRANSACTION_URL = "api/v2/quickteller/transactions?requestreference=";
    public final static String BILL_PAYMENT_INQUIRY_URL = "api/v2/quickteller/transactions/inquirys";
    public final static String SEND_BILL_PAYMENT_TRANSACTION_URL = "api/v2/quickteller/transactions";
    public final static String CUSTOMER_VALIDATION_URL = "api/v2/quickteller/customers/validations";
    
    //payment channels
    public static final String CHANNEL_ATM = "1";
    public static final String CHANNEL_POS = "2";
    public static final String CHANNEL_WEB = "3";
    public static final String CHANNEL_MOBILE = "4";
    public static final String CHANNEL_KIOSK = "5";
    public static final String CHANNEL_VOICE = "6";
    public static final String CHANNEL_LOCATION = "7";
       
	
	//public final String CLIENT_ID = "IKIA9614B82064D632E9B6418DF358A6A4AEA84D7218";
	//public final String CLIENT_SECRET = "XCTiBtLy1G9chAnyg0z3BcaFK4cVpwDg/GTw2EmjTZ8=";
	
    //other header constants
    public static final String COUNTRY_CODE = "NG";
    public static final String CURRENCY_CODE = "NGN";
    public static final String CURRENCY_CODE_NUMBER = "566";
    public static final String TIME_STAMP = timeStamp();
    public static final String NONCE = nonce();
    public static final String CONTENT_TYPE = "application/json";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String INITIATING_PAYMENT_METHOD_CODE = "CA";
    public static final String TERMINATING_PAYMENT_METHOD_CODE = "AC";
    public static final String ACCOUNT_TYPE_SAVINGS = "10";
    public static final String ACCOUNT_TYPE_CURRENT = "20";
    public static final String ACCOUNT_TYPE_DEFAULT = "00";
    
    //success/pending response codes
    public static final String SUCCESS_CODES = "90000 90010 90011 90016 90009 900A0 70022 10001 E18 90E18 E20 90E20 E21 90E21";
    
    
    //no classification
    private String baseUrl;						// = "https://sandbox.interswitchng.com/";
    private String clientId;					// = "IKIA9614B82064D632E9B6418DF358A6A4AEA84D7218";
    private String clientSecret;				// = "XCTiBtLy1G9chAnyg0z3BcaFK4cVpwDg/GTw2EmjTZ8=";
    private String initiatingEntityCode;		// = "ERT";
    private String transferCodePrefix;			// = "1413";
    private String terminalId;					// = "3ERT0001";
    private String signatureMethod;				// = "SHA1";// "SHA-256";
    private String authorization;
    
	

        
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    public QuicktellerConstants() {
        this.baseUrl = "https://sandbox.interswitchng.com/";
        this.clientId = "IKIA9614B82064D632E9B6418DF358A6A4AEA84D7218";
        this.clientSecret = "XCTiBtLy1G9chAnyg0z3BcaFK4cVpwDg/GTw2EmjTZ8=";
        this.authorization = "InterswitchAuth " + new String(Base64.encode(this.clientId.getBytes()));
        this.initiatingEntityCode = "ERT";
        this.transferCodePrefix = "1413";
        this.terminalId = "3ERT0001";
        this.signatureMethod = "SHA1";
    }
    
    public QuicktellerConstants(String baseUrl, String clientId, String clientSecret, String initiatingEntityCode,
    		String transferCodePrefix, String terminalId, String signatureMethod) {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorization = "InterswitchAuth " + new String(Base64.encode(clientId.getBytes()));
        this.initiatingEntityCode = initiatingEntityCode;
        this.transferCodePrefix = transferCodePrefix;
        this.terminalId = terminalId;
        this.signatureMethod = signatureMethod;
    }
    
    public QuicktellerConstants baseUrl(String baseUrl) {
    	this.baseUrl = baseUrl;
    	return this;
    }
    
    public QuicktellerConstants clientId(String clientId) {
    	this.clientId = clientId;
    	this.authorization = "InterswitchAuth " + new String(Base64.encode(clientId.getBytes()));
    	return this;
    }
    
    public QuicktellerConstants clientSecret(String clientSecret) {
    	this.clientSecret = clientSecret;
    	return this;
    }
        
    public QuicktellerConstants initiatingEntityCode(String initiatingEntityCode) {
    	this.initiatingEntityCode = initiatingEntityCode;
    	return this;
    }
    
    public QuicktellerConstants transferCodePrefix(String transferCodePrefix) {
    	this.transferCodePrefix = transferCodePrefix;
    	return this;
    }
    
    public QuicktellerConstants terminalId(String terminalId) {
    	this.terminalId = terminalId;
    	return this;
    }
    
    public QuicktellerConstants signatureMethod(String signatureMethod) {
    	this.signatureMethod = signatureMethod;
    	return this;
    }
    
    
    public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getInitiatingEntityCode() {
		return initiatingEntityCode;
	}

	public void setInitiatingEntityCode(String initiatingEntityCode) {
		this.initiatingEntityCode = initiatingEntityCode;
	}

	public String getTransferCodePrefix() {
		return transferCodePrefix;
	}

	public void setTransferCodePrefix(String transferCodePrefix) {
		this.transferCodePrefix = transferCodePrefix;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getSignatureMethod() {
		return signatureMethod;
	}

	public void setSignatureMethod(String signatureMethod) {
		this.signatureMethod = signatureMethod;
	}

	public String getAuthorization() {
		return authorization;
	}
	
	
	
	
	public static String timeStamp() {
    	// Timestamp
		TimeZone lagosTimeZone = TimeZone.getTimeZone("Africa/Lagos");
		Calendar calendar = Calendar.getInstance(lagosTimeZone);
		long timestamp = calendar.getTimeInMillis() / 1000;
		return timestamp+"";
    }
    
    public static String nonce() {
    	// Nonce
		UUID uuid = UUID.randomUUID();
		String nonce = uuid.toString().replaceAll("-", "");
		return nonce;
    }
    
    public static String signature(String signatureCipher, String algorithm){
    	// Signature
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance(algorithm);
			byte[] signatureBytes = messageDigest.digest(signatureCipher.getBytes());
			String signature = new String(Base64.encode(signatureBytes)).trim().replaceAll("\\s", "");;
			return signature;
			
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
		
    }
        
	public static String SHA256(String valueToHash) {
		return SHAHashValue(valueToHash, "SHA-256");
	}
	
	public static String SHA512(String valueToHash) {
		return SHAHashValue(valueToHash, "SHA-512");
	}

	private static String SHAHashValue(String cipherValue, String algorithm) {
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			// md.update(salt);
			byte[] bytes = md.digest(cipherValue.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}  

    
	
//  public static String SHA512(String signatureCipher) throws Exception{
//	MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
//	byte[] signatureBytes = messageDigest.digest(signatureCipher.getBytes("UTF-8"));
//	
//	String signature = bytesToHex(signatureBytes);
//	return signature;
//}
//    
//private static String bytesToHex(byte[] bytes) {
//    char[] hexChars = new char[bytes.length * 2];
//    for ( int j = 0; j < bytes.length; j++ ) {
//        int v = bytes[j] & 0xFF;
//        hexChars[j * 2] = hexArray[v >>> 4];
//        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
//    }
//    return new String(hexChars);
//}
	

}
