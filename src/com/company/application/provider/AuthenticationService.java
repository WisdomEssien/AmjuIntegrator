package com.company.application.provider;

import java.io.IOException;
import java.util.Base64;
import java.util.StringTokenizer;

import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.neptunesoftware.reuseableClasses.Quickteller.data.RubikonCredential;

public class AuthenticationService {
	public boolean authenticate(String authCredentials) {

		// read username, password and whether service should be authenticated from RubikonInfo.xml file
		RubikonCredential rubikonCredential = Quickteller.readRubikonConfig();
		
		boolean response = rubikonCredential.getAuthenticateService().trim().equalsIgnoreCase("true") ||
				rubikonCredential.getAuthenticateService().trim().equalsIgnoreCase("yes") ? false : true;
		
		if (null == authCredentials)
			return response;
		// header value format will be "Basic encodedstring" for Basic authentication.
		// Example "Basic YWRtaW46YWRtaW4="
		final String encodedUserPassword = authCredentials.replaceFirst("Basic"
				+ " ", "");
		String usernameAndPassword = null;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(
					encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
		} catch (IOException e) {
			System.out.println("Exception: could not decode username and password");
			//e.printStackTrace();
		}
		final StringTokenizer tokenizer = new StringTokenizer(
				usernameAndPassword, ":");
		final String username = tokenizer.nextToken();
		final String password = tokenizer.nextToken();
		
		String readUsername = rubikonCredential.getAuthenticatedUsername();
		String readPasword = rubikonCredential.getAuthenticatedPassword();
		
		boolean authenticationStatus = readUsername.equals(username)
				&& readPasword.equals(password);
		return authenticationStatus;
	}
}
