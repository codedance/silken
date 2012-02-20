/*
* (c) Copyright 1999-2012 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.papercut.silken.test;

import java.util.Map;

import com.google.common.collect.Maps;

public class PojoWithComplexMap {
	private Map<String, SimplePojo> complexMap;
	
	public PojoWithComplexMap() {
		complexMap = Maps.newHashMap();
		
		complexMap.put("use", new SimplePojo("words", 1, 123l));
		complexMap.put("the", new SimplePojo("words", 2, 456l));
		complexMap.put("force", new SimplePojo("words", 3, 789l));
	}

	public Map<String, SimplePojo> getComplexMap() {
		return complexMap;
	}
}
