package com.neptunesoftware.reuseableClasses;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


//import org.eclipse.jetty.util.ajax.JSON;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class CommonMethods {

	public static String getInfo(String name, Class<?> clazz) {
		try {
			InputStream is = clazz.getResourceAsStream(name);
			byte[] data = new byte[is.available()];
			is.read(data);
			is.close();
			String content = new String(data);
			return content;
		} catch (Exception ex) {
			System.out.println("Input File Not Found");
			//ex.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T xmlStringToObject(String xmlString, Class<?> clazz) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(clazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		StreamSource streamSource = new StreamSource(new StringReader(xmlString));
		JAXBElement<T> jxb = (JAXBElement<T>) unmarshaller.unmarshal(streamSource, clazz);
		return jxb.getValue();
	}
	
	public static String objectToXml(Object response) {
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);

		String xmlString = "";
		try {
			JAXBContext contextObj = JAXBContext.newInstance(response.getClass());
			Marshaller marshallerObj = contextObj.createMarshaller();
			marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshallerObj.marshal(response, result);
			// marshallerObj.marshal(integratorResponse, System.out);
			xmlString = readFromStringWriter(stringWriter.getBuffer().toString());
		} catch (Exception e) {
		}

		// JAXB.unmarshal(new StringReader(body), CustomerInformationRequest.class);
		// //or simply use this
		return xmlString;
	}
	
	private static String readFromStringWriter(String inputString)  {
		StringReader stringReader = new StringReader(inputString);
		StringBuilder builder = new StringBuilder();

		int charsRead = -1;
		char[] chars = new char[100];
		do {
			try {
				charsRead = stringReader.read(chars, 0, chars.length);
			} catch (IOException e) {}
			
			// if we have valid chars, append them to end of string.
			if (charsRead > 0)
				builder.append(chars, 0, charsRead);
		} while (charsRead > 0);

		return builder.toString();
	}

	public static Object JSONStringToObject(String jsonStr, Class<?> clazz) {
		Gson gson = new Gson();
		Object JavaObject = gson.fromJson(jsonStr, clazz);
		return JavaObject;
	}

	public static String ObjectToJsonString(Object clazz) {
		// Convert object to JSON string
		Gson gson = new GsonBuilder()
				//.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.setPrettyPrinting()
				.create();
//		Gson gson = new Gson();
		String json = gson.toJson(clazz);
		return json;
	}
	
	public static String zeroPadding(String valueToPad, int neededSignificantValue, boolean isBefore) {
		
		String zeros = "", padedValue = valueToPad;
		while (neededSignificantValue-- > 0) {
			zeros += "0";
		}

		padedValue = zeros + valueToPad;
		
		if (!isBefore) {
			padedValue += zeros;
		}
		
		return padedValue;		
	}
	
//	public static void main(String[] arg) {
//		System.out.println(zeroPadding("1", 3 - "1".length(), true));
//	}

	// Should return an object
	public static Object JSONOrXMLToObject(String message, Class<?> clazz) throws Exception {
		Object messageObject = null;

		if (IsJson(message) == true) {
			// call the JSON to Object method.
			messageObject = JSONStringToObject(message, clazz);
		} else if (IsXml(message) == true) {
			// call the XML to Object method
			messageObject = xmlStringToObject(message, clazz);
		} else {
			// Return a message string.
			System.out.println( "The Request is Invalid : " + message);
		}

		return messageObject;
		// At the end of each methods, call the MEdiaType(Object to JSON or XML) method
		// to return result in the IntegrationSoapImpl class.
	}

	// Should return a String. Either an XML or JSON format.
	public static String ObjectToJSONOrXMLstring(String request, Object response) throws Exception {
		String messageObject = null;

		if (IsJson(request) == true) {
			// call the JSON to Object method.
			messageObject = ObjectToJsonString(response);
			System.out.println("Object is converted to JSON");
			
			return messageObject;
		}

		if (IsXml(request) == true) {
			// call the XML to Object method
			messageObject = objectToXml(response);
			System.out.println("Object is converted to XML");
			
			return messageObject;
		}

		return messageObject;
		// At the end of each methods, call this MEdiaType(Object to JSON or XML) method
		// to return result in the IntegrationSoapImpl class.
	}

	
	// Checks if the request is JSON format
	  public static boolean IsJson(String jsonInString) {
		  Gson gson = new Gson();
	      try {
	          gson.fromJson(jsonInString, Object.class);
	          return true;
	      } catch(JsonSyntaxException ex) { 
	          return false;
	      }
	  }
	  
	// Checks if the request is XML format
	public static boolean IsXml(String message) throws Exception {
		if (message.startsWith("<")) {
			try {
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(new StringReader(message)));
				// System.out.println("Message is valid XML.");
			} catch (Exception e) {
				// System.out.println("Message is not valid XML.");
				return false;
			}
		} else {
			return false;
		}

		return true;
	}
		
    public static double koboToNaira(int amountInKobo) {
    	double amountInNaira = amountInKobo / 100;
    	return amountInNaira;
    }
    
    public static double koboToNaira(double amountInKobo) {
    	double amountInNaira = amountInKobo / 100;
    	return amountInNaira;
    }
    
    public static double nairaToKobo(int amountInNaira) {
    	double amountInKobo  = amountInNaira * 100;
    	return amountInKobo;
    }    
    
    public static double nairaToKobo(double amountInNaira) {
    	double amountInKobo  = amountInNaira * 100;
    	return amountInKobo;
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

    public static void disableSSL() throws IOException{
    	try{
		     // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                    return null;
	                }
	                public void checkClientTrusted(X509Certificate[] certs, String authType) {
	                }
	                public void checkServerTrusted(X509Certificate[] certs, String authType) {
	                }
	            }
	        };
	 
	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	 
	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };
	 
	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		}
		catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (KeyManagementException e) {
	        e.printStackTrace();
	    }
	}
	
	
	public static String convertDateFormatToString(String inputDate, String sourceFormat, String neededFormat) {
		SimpleDateFormat sourcedf = new SimpleDateFormat(sourceFormat);
		SimpleDateFormat destdf = new SimpleDateFormat(neededFormat);
		Date javaDate = getCurrentDate();
		String formattedDate = "";
		formattedDate = destdf.format(javaDate);
		
		try {
			if (inputDate.trim().length() > 0)
				formattedDate = destdf.format(sourcedf.parse(inputDate));
		} catch (ParseException e) {
			System.out.println("Exception was thrown. Current date will be retuned instead");
		}
	    return formattedDate;
	}
    
	public static Date convertDateFormatToDate(String inputDate, String sourceFormat, String neededFormat) {
		SimpleDateFormat sourcedf = new SimpleDateFormat(sourceFormat);
		SimpleDateFormat destdf = new SimpleDateFormat(neededFormat);
		Date javaDate = getCurrentDate();
		
		try {
			if (inputDate.trim().length() > 0)
				javaDate = destdf.parse(destdf.format(sourcedf.parse(inputDate)));
		} catch (ParseException e) {
			System.out.println("Exception was thrown. Current date will be returned instead");
		}
	    return javaDate;
	}
	
	private static Date getCurrentDate() {
	    java.util.Date today = new java.util.Date();
	    return today;
	}
	
	public static String getCurrentDateAsString(String inputDate, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date javaDate = getCurrentDate();
		String formattedDate = "";
		formattedDate = sdf.format(javaDate);
		
		try {
			if (inputDate.trim().length() > 0)
				formattedDate = sdf.format(sdf.parse(inputDate));
		} catch (ParseException e) {
		}
	    return formattedDate;
	}
	
	public static Date getCurrentDateAsDate(String inputDate, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date javaDate = getCurrentDate();
		
		try {
			if (inputDate.trim().length() > 0)
				javaDate = sdf.parse(inputDate);
		} catch (ParseException e) {
		}
	    return javaDate;
	}

	public static String addMonth(String dateAsString, String dateFormat, int months) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date javaDate = getCurrentDate();
		//String formattedDate = sdf.format(javaDate);

		try {
			javaDate = sdf.parse(dateAsString);
		} catch (ParseException e) {}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(javaDate);
		cal.add(Calendar.MONTH, months);
		Date dateAsObjAfterAMonth = cal.getTime();
		return sdf.format(dateAsObjAfterAMonth);
	}
	
	public static Date addDays(String dateAsString, String dateFormat, int days) {
		
		Date passedDate = getCurrentDateAsDate(dateAsString, dateFormat);
		//DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(dateFormat);
		
		// convert date to LocalDateTime
		LocalDateTime localDateTime = passedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
		// add the days
		localDateTime = localDateTime.plusDays(days);

        // convert LocalDateTime to date
        Date passedDatePlusDay = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        return passedDatePlusDay;
	}
	
	public static Date addDays(Date date, String dateFormat, int days) {
				
		// convert date to LocalDateTime
		LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
		// add the days
		localDateTime = localDateTime.plusDays(days);

        // convert LocalDateTime to date
        Date passedDatePlusDay = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        return passedDatePlusDay;
	}
	
	public static Date addMonths(String dateAsString, String dateFormat, int months) {
		
		Date passedDate = getCurrentDateAsDate(dateAsString, dateFormat);
		//DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(dateFormat);
		
		// convert date to LocalDateTime
		LocalDateTime localDateTime = passedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
		// add the days
		localDateTime = localDateTime.plusMonths(months);

        // convert LocalDateTime to date
        Date passedDatePlusMonth = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        return convertDateFormatToDate(passedDatePlusMonth+"", "E MMM dd HH:mm:ss Z yyyy", dateFormat);
	}
	
	public static Date addMonths(Date date, String dateFormat, int months) {
		
		// convert date to LocalDateTime
		LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
		// add the days
		localDateTime = localDateTime.plusMonths(months);

        // convert LocalDateTime to date
        Date passedDatePlusMonth = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        return getCurrentDateAsDate(passedDatePlusMonth+"", dateFormat);
	}
	
	public static void logContent(String content) {		
		try {
			
			String todayDate = CommonMethods.getCurrentDateAsString("", "dd-MM-yyyy");
			String fileName = "AmjuIntegrator-" + todayDate + ".log";
			
			FileOutputStream file = new FileOutputStream(fileName, true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file, StandardCharsets.UTF_8);
			Writer writer = new BufferedWriter(outputStreamWriter);
			writer.write(getCurrentDateAsString("", "dd-MMM-yyyy hh:mm:ss:ssss"));
			writer.write("\r\n" + content + "\r\n\r\n");
			
			writer.close();
			outputStreamWriter.close();
			file.close();
		} 
		catch (Exception ex) {
			System.out.println("contents not logged!");
		}		
	}
	
	public static void logSensitiveContent(String content) {		
		try {
			
			String todayDate = CommonMethods.getCurrentDateAsString("", "dd-MM-yyyy");
			String fileName = "DeviceAmjuIntegrator-" + todayDate + ".log";
			
			FileOutputStream file = new FileOutputStream(fileName, true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file, StandardCharsets.UTF_8);
			Writer writer = new BufferedWriter(outputStreamWriter);
			writer.write(getCurrentDateAsString("", "dd-MMM-yyyy hh:mm:ss:ssss"));
			writer.write("\r\n" + content + "\r\n\r\n");
			
			writer.close();
			outputStreamWriter.close();
			file.close();
		} 
		catch (Exception ex) {
			System.out.println("contents not logged!");
		}		
	}
	
	
	public static void main(String[] args) {
//		DBConnection oracleDB = new DBConnection("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@localhost:1521/orclpdb", "neptunelive",
//				"neptune", "ORACLE");
		
		String processingDate = "20181027";
		try {
			//processingDate = oracleDB.selectProcessingDate();
		} catch (Exception e) {
		}
		String startDate = CommonMethods.getCurrentDateAsString("", "yyyy-MM-dd");
		//String formattedDate = CommonMethods.convertDateFormatToString(processingDate, "yyyyMMdd", "yyyy-MM-dd");
		//LocalDate date = LocalDate.parse(formattedDate).minusMonths(1);
		
		String endDate = CommonMethods.addMonth(startDate, "yyyy-MM-dd", 1) + "";
		
//		//System.out.println(getCurrentDate("20190730","yyyyMMdd"));
//		System.out.println("processing Date: " + processingDate);
		System.out.println("start Date: " + startDate);
//		System.out.println("formattedDate: " + formattedDate);
//		System.out.println("date: " + date);
		System.out.println("End date: " + endDate);
	}


}
