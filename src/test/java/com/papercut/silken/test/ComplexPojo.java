/*
* (c) Copyright 1999-2012 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.papercut.silken.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplexPojo {
    
	private SimplePojo nestedSimplePojo;
	private ComplexPojo nestedComplexPojo;
	
	private Map<String, String> nestedMapString = new HashMap<String, String>();
	private Map<String, SimplePojo> nestedMapPojo = new HashMap<String, SimplePojo>();
	
	private List<String> nestedListSimple = new ArrayList<String>();
	
	private List<SimplePojo> nestedListPojo = new ArrayList<SimplePojo>();
	
	private int[] intArray;

    public SimplePojo getNestedSimplePojo() {
        return nestedSimplePojo;
    }

    public void setNestedSimplePojo(SimplePojo nestedSimplePojo) {
        this.nestedSimplePojo = nestedSimplePojo;
    }

    public ComplexPojo getNestedComplexPojo() {
        return nestedComplexPojo;
    }

    public void setNestedComplexPojo(ComplexPojo nestedComplexPojo) {
        this.nestedComplexPojo = nestedComplexPojo;
    }

    public Map<String, String> getNestedMapString() {
        return nestedMapString;
    }

    public void setNestedMapString(Map<String, String> nestedMapString) {
        this.nestedMapString = nestedMapString;
    }

    public Map<String, SimplePojo> getNestedMapPojo() {
        return nestedMapPojo;
    }

    public void setNestedMapPojo(Map<String, SimplePojo> nestedMapPojo) {
        this.nestedMapPojo = nestedMapPojo;
    }

    public List<String> getNestedListSimple() {
        return nestedListSimple;
    }

    public void setNestedListSimple(List<String> nestedListSimple) {
        this.nestedListSimple = nestedListSimple;
    }

    public List<SimplePojo> getNestedListPojo() {
        return nestedListPojo;
    }

    public void setNestedListPojo(List<SimplePojo> nestedListPojo) {
        this.nestedListPojo = nestedListPojo;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }
	
	

}
