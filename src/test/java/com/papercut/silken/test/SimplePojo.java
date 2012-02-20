/*
* (c) Copyright 1999-2012 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.papercut.silken.test;

public class SimplePojo {
	private String stringValue;
	private int intValue;
	private Long longValue;
	
	public SimplePojo() {}
	public SimplePojo(String stringValue, int intValue, long longValue) {
		this.stringValue = stringValue;
		this.intValue = intValue;
		this.longValue = longValue;
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
	public Long getLongValue() {
		return longValue;
	}
	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}
}
