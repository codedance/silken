/*
* (c) Copyright 1999-2012 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.papercut.silken.test;

public class ComplexPojo {
	private SimplePojo simple = new SimplePojo();
	private String name = "Complex pojo";
	
	public ComplexPojo() {
		simple.setIntValue(1);
		simple.setStringValue("String");
		simple.setLongValue(123l);
	}

	public SimplePojo getSimple() {
		return simple;
	}

	public void setSimple(SimplePojo simple) {
		this.simple = simple;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
