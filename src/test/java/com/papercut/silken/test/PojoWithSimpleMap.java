/*
* (c) Copyright 1999-2012 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.papercut.silken.test;

import java.util.Map;

import com.google.common.collect.Maps;

public class PojoWithSimpleMap {
	private Map<String, Integer> simpleMap;
	
	public PojoWithSimpleMap() {
		simpleMap = Maps.newHashMap();
		
		simpleMap.put("testing", 1);
		simpleMap.put("is", 2);
		simpleMap.put("fun", 3);
	}

	public Map<String, Integer> getSimpleMap() {
		return simpleMap;
	}

	public void setSimpleMap(Map<String, Integer> simpleMap) {
		this.simpleMap = simpleMap;
	}
}
