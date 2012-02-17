/*
* (c) Copyright 1999-2012 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.papercut.silken.test;

import java.util.List;

import com.google.common.collect.Lists;

public class SuperComplexPojo {
	private ComplexPojo complex = new ComplexPojo();
	private String name = "double nested";
	private List<SimplePojo> pojoList = Lists.newArrayList();
	
	public SuperComplexPojo() {
		this.pojoList.add(new SimplePojo());
		this.pojoList.add(new SimplePojo());
		this.pojoList.add(new SimplePojo());
	}
	
	public ComplexPojo getComplex() {
		return complex;
	}
	public void setComplex(ComplexPojo complex) {
		this.complex = complex;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public List<SimplePojo> getPojoList() {
		return pojoList;
	}

	public void setPojoList(List<SimplePojo> pojoList) {
		this.pojoList = pojoList;
	}
}
