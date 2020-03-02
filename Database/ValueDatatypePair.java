package com.neptunesoftware.reuseableClasses.Database;

import java.sql.Types;

public class ValueDatatypePair {
	
	/* 
	 * List of TYPES that can be passed for the type parameter
	 * 
	 * FOR STRING VALUES USE
	 * 		Types.VARCHAR
	 * 
	 * FOR DECIMAL/ BIG DECIMAL VALUES USE ANY OF THE FOLLOWING
	 * 		Types.DECIMAL
	 * 		Types.NUMERIC
	 * 
	 * FOR DOUBLE VALUES USE
	 * 		Types.DOUBLE
	 * 
	 * FOR FLOATING POINT VALUES USE
	 * 		Types.FLOAT
	 * 
	 * FOR INTEGER VALUES USE ANY OF THE FOLLOWING
	 * 		Types.INTEGER
	 * 		Types.BIGINT
	 * 		Types.SMALLINT
	 * 
	 * FOR DATE VALUES USE
	 * 		Types.DATE
	 * 
	 * 
	 * */
	
	private String value;
	private int type;
	
	public ValueDatatypePair() {
	}	
	
	public ValueDatatypePair Value(Object value) {
		String temp = value + ""; //converts to string
		this.value = temp.equalsIgnoreCase("null") ? "" : temp;
		return this;
	}
	
	public ValueDatatypePair Type(int type) {
		this.type = type;
		return this;
	}
	
	public ValueDatatypePair(Object value) {
		String temp = value + ""; //converts to string
		this.value = temp.equalsIgnoreCase("null") ? "" : temp;
		this.type = Types.VARCHAR;
	}
	
	public ValueDatatypePair(Object value, int type) {
		super();
		String temp = value + ""; //converts to string
		this.value = temp.equalsIgnoreCase("null") ? "" : temp;
		this.type = type;
	}
	
	// This constructor can ONLY be used for creating out parameters
	public ValueDatatypePair(int type) {
		super();
		this.value = "";
		this.type = type;
	}
	
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	
}
