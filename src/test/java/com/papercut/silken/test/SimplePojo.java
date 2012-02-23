/*
* (c) Copyright 1999-2012 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.papercut.silken.test;

public class SimplePojo {
	private String stringValue;
	private int intValue;
	private Boolean boolValue;
	
	public SimplePojo() {}
	public SimplePojo(String stringValue, int intValue, boolean boolValue) {
		this.stringValue = stringValue;
		this.intValue = intValue;
		this.boolValue = boolValue;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public int getIntValue() {
		return intValue;
	}
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
    public Boolean getBoolValue() {
        return boolValue;
    }
    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }
	
}
