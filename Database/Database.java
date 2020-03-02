package com.neptunesoftware.reuseableClasses.Database;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "Databases")
@XmlAccessorType(XmlAccessType.FIELD)
public class Database {

	@XmlElement(name = "Database")
	private List<DatabaseProperty> databaseProps;
	
	@XmlTransient
	private String responseCode;
	
	public Database() {
	}
	
	public Database(List<DatabaseProperty> databaseProps) {
		super();
		this.databaseProps = databaseProps;
	}

	public List<DatabaseProperty> getDatabaseProps() {
		return databaseProps;
	}

	public void setDatabaseProps(List<DatabaseProperty> databaseProps) {
		this.databaseProps = databaseProps;
	}

	public String getResponseCode() {
		return responseCode == null ? "" : responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	
	
}
