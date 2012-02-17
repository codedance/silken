/*
* (c) Copyright 1999-2012 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.papercut.silken;

import java.util.Map;

import junit.framework.Assert;

import static junit.framework.Assert.*;

import org.junit.Test;

import com.google.template.soy.data.SoyListData;
import com.papercut.silken.test.ComplexPojo;
import com.papercut.silken.test.SuperComplexPojo;
import com.papercut.silken.test.SimpleArrayPojo;
import com.papercut.silken.test.SimplePojo;

public class UtilsTest {

	@Test
	public void pojoToMap_simplePojo_createsMap() {
		// arrange
		SimplePojo pojo = new SimplePojo();
		
		// act
		Map<String, ?> resultMap = Utils.pojoToMap(pojo);
		
		// assert
		assertTrue(resultMap.keySet().size() == 3);
	}
	
	@Test
	public void pojoToMap_simplePojo_getsInts() {
		// arrange
		int iValue = 3;
		String sValue = "string stuff";
		Long lValue = 1234l;
		
		SimplePojo pojo = new SimplePojo();
		pojo.setIntValue(iValue);
		pojo.setLongValue(lValue);
		pojo.setStringValue(sValue);

		// act
		Map<String, ?> result = Utils.pojoToMap(pojo);

		// assert 
		assertEquals(iValue, result.get("intValue"));
	}
	
	@Test
	public void pojoToMap_simplePojo_getsLongs() {
		// arrange
		int iValue = 3;
		String sValue = "string stuff";
		Long lValue = 1234l;
		
		SimplePojo pojo = new SimplePojo();
		pojo.setIntValue(iValue);
		pojo.setLongValue(lValue);
		pojo.setStringValue(sValue);

		// act
		Map<String, ?> result = Utils.pojoToMap(pojo);

		// assert 
		assertEquals(lValue, result.get("longValue"));
	}
	
	@Test
	public void pojoToMap_simplePojo_getsStrings() {
		// arrange
		int iValue = 3;
		String sValue = "string stuff";
		Long lValue = 1234l;
		
		SimplePojo pojo = new SimplePojo();
		pojo.setIntValue(iValue);
		pojo.setLongValue(lValue);
		pojo.setStringValue(sValue);

		// act
		Map<String, ?> result = Utils.pojoToMap(pojo);

		// assert 
		assertEquals(sValue, result.get("stringValue"));
	}
	
	@Test
	public void pojoToMap_simpleArrayPojo_createsMap() {
		// arrange
		SimpleArrayPojo pojo = new SimpleArrayPojo();

		// act
		Map<String, ?> result = Utils.pojoToMap(pojo);

		// assert 
		assertEquals(2, result.size());
	}
	
	@Test
	public void pojoToMap_simpleArrayPojo_getsIntArray() {
		// arrange
		SimpleArrayPojo pojo = new SimpleArrayPojo();

		// act
		Map<String, ?> result = Utils.pojoToMap(pojo);
		
		// assert 
		assertTrue(result.get("intArray").getClass().isArray());
		assertEquals(3, ((int[])result.get("intArray"))[2]);
	}
	
	
	@Test
	public void pojoToMap_complexPojo_createsMap() {
		// arrange
		ComplexPojo pojo = new ComplexPojo();

		// act
		Map<String, ?> result = Utils.pojoToMap(pojo);
		
		// assert
		assertEquals(2, result.size());
	}
	
	@Test
	public void pojoToMap_complexPojo_getsSimplePojo() {
		// arrange
		ComplexPojo pojo = new ComplexPojo();

		// act
		Map<String, ?> result = Utils.pojoToMap(pojo);
		
		// assert
		assertTrue(result.get("simple") instanceof Map<?, ?>);
	}
	
	@Test
	public void pojoToMap_superComplexPojo_createsMap() {
		// arrange
		SuperComplexPojo pojo = new SuperComplexPojo();

		// act
		Map<String, ?> result = Utils.pojoToMap(pojo);
		
		// assert
		assertEquals(3, result.size());
	}
}
