package com.neptunesoftware.reuseableClasses.WebserviceCall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;


public class WebserviceCall {
	
	static HttpGet httpget = null;
	static HttpPost httppost = null;
	static CloseableHttpClient httpclient = HttpClients.createDefault();
	
	private String baseUrl;
	
	public WebserviceCall() {
		this.baseUrl = "";
	}
	
	public WebserviceCall(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public HttpResponse getMethod(String path, HashMap<String, String> extraHeaders) {
		String url = baseUrl + path;
		
		httpget = new HttpGet(url);

		//Compute headers
		httpget = (HttpGet) computeHeaders(url, httpget, extraHeaders);
		
		return returnContent(httpget);
	}
	
	public HttpResponse postMethod(String path, String body, HashMap<String, String> extraHeaders) {

		String url = baseUrl + path;
		
		httppost = new HttpPost(url);
		
		httppost = (HttpPost) computeHeaders(url, httppost, extraHeaders);
		
		//httppost.setEntity(new UrlEncodedFormEntity(body));
		httppost.setEntity(new StringEntity(body, "UTF-8"));
		
		return returnContent(httppost);
	}
	
	private static HttpResponse returnContent(HttpUriRequest request) {	
		//static CloseableHttpResponse response = null;

		HttpResponse httpResponse = new HttpResponse();
		int statusCode = 0;
		StringBuffer result = new StringBuffer();
				
		try {
			CloseableHttpResponse response = httpclient.execute(request);

			statusCode = response.getStatusLine().getStatusCode();
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			response.close();
			
			// OK
			httpResponse.setStatusCode(statusCode);
			httpResponse.setResponseBody(result.toString());
			
		} catch (IOException e) {
			// Request not executed			
			httpResponse.setStatusCode(-1);
			httpResponse.setResponseBody(e.getMessage());
			
		} catch (Exception e) {
			// exception while reading Response
			httpResponse.setStatusCode(statusCode);
			httpResponse.setResponseBody(e.getMessage());
		}
		
		
		return httpResponse;
    }

	@SuppressWarnings({ "rawtypes", "unchecked"})
	private static HttpUriRequest computeHeaders(String url, HttpUriRequest request, HashMap<String, String> extraHeaders) {
		
		//Compute headers
		
		if ((extraHeaders != null) && (extraHeaders.size() > 0))
	    {
	      Iterator<?> it = extraHeaders.entrySet().iterator();
	      while (it.hasNext())
	      {
	        Map.Entry<String, String> pair = (Map.Entry)it.next();
	        request.setHeader(pair.getKey(), pair.getValue());
	        //System.out.println(""+pair.getKey() + ": " +pair.getValue());
	      }
	    }
		
		return request;
	}
	
	
	
	
	public static String soapWebService(String wsdlUrl, SOAPMessage soapRequest) throws Exception {
		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory.createConnection();

		//
		URL endpoint = new URL(wsdlUrl.trim());
		SOAPMessage soapResponse = soapConnection.call(soapRequest, endpoint);
		String strResult = createSoapResponse(soapResponse);
		soapConnection.close();

		return strResult;
	}

	private static String createSoapResponse(SOAPMessage soapResponse) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		Document doc = soapResponse.getSOAPBody().extractContentAsDocument();
		
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		//transformer.transform(sourceContent, result);
		
		transformer.transform(new DOMSource(doc), result);

		return writer.toString();
	}

	public static Object createSoapResponseObject(String wsdlUrl, SOAPMessage soapRequest) throws Exception {
		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory.createConnection();

		// hit soapRequest to the server to get response 
		SOAPMessage soapResponse = soapConnection.call(soapRequest, wsdlUrl);
		Object Content = soapResponse.getSOAPBody().extractContentAsDocument();
		soapConnection.close();
		return Content;
	}
	

	
}





