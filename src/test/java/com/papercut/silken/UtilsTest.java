/*
* (c) Copyright 1999-2012 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.papercut.silken;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.papercut.silken.test.ComplexPojo;
import com.papercut.silken.test.SimplePojo;

public class UtilsTest {
    
    private SimplePojo simplePojo;
    private ComplexPojo complexPojo;
    
    @Before
    public void setup() {
        
        simplePojo = new SimplePojo();
        simplePojo.setBoolValue(false);
        simplePojo.setStringValue("simple-string");
        simplePojo.setIntValue(12345);
        
        complexPojo = new ComplexPojo();
        
        complexPojo.setIntArray(new int[] {1, 2, 3, 4, 5});
        
        complexPojo.setNestedSimplePojo(simplePojo);
        
        complexPojo.setNestedMapString(ImmutableMap.of("key1", "value1", "key2", "value2"));
        
        complexPojo.setNestedMapPojo(ImmutableMap.of("key1", simplePojo));
        
        complexPojo.setNestedListPojo(Lists.newArrayList(simplePojo));
        
        complexPojo.setNestedListSimple(Lists.newArrayList("a", "b", "c"));
        
        
        ComplexPojo complexPojo2 = new ComplexPojo();
        complexPojo2.setNestedListSimple(Lists.newArrayList("nested list value"));
        complexPojo2.setNestedSimplePojo(simplePojo);
        
        complexPojo.setNestedComplexPojo(complexPojo2);
    }
    
	@Test
	public void toSoyCompatibleMap_simplePojo_createsMap() {
		// arrange
		SimplePojo pojo = new SimplePojo();
		
		// act
		Map<String, ?> resultMap = Utils.toSoyCompatibleMap(pojo);
		
		// assert
		assertTrue(resultMap.keySet().size() == 3);
	}
	
	@Test
	public void toSoyCompatibleMap_simplePojo_getsInts() {
		// arrange
		// act
		Map<String, ?> result = Utils.toSoyCompatibleMap(simplePojo);

		// assert 
		assertEquals(simplePojo.getIntValue(), result.get("intValue"));
	}
	
	@Test
	public void toSoyCompatibleMap_simplePojo_getsBools() {
		// act
		Map<String, ?> result = Utils.toSoyCompatibleMap(simplePojo);

		// assert 
		assertEquals(simplePojo.getBoolValue(), result.get("boolValue"));
	}
	
	@Test
	public void toSoyCompatibleMap_simplePojo_getsStrings() {
		// arrange
		// act
		Map<String, ?> result = Utils.toSoyCompatibleMap(simplePojo);

		// assert 
		assertEquals(simplePojo.getStringValue(), result.get("stringValue"));
	}
	
	@Test
	public void toSoyCompatibleMap_simpleArrayPojo_getsIntArray() {
		// arrange
		// act
		Map<String, ?> result = Utils.toSoyCompatibleMap(complexPojo);
		
		// assert 
		assertTrue(result.get("intArray").getClass().isArray());
		assertEquals(3, ((int[])result.get("intArray"))[2]);
	}
	
	
	@Test
	public void toSoyCompatibleMap_complexPojo_nestedMap() {
 
		// act
		Map<String, ?> result = Utils.toSoyCompatibleMap(complexPojo);
		
		// assert
		@SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) result.get("nestedMapString");
		assertEquals(nestedMap.get("key1"), complexPojo.getNestedMapString().get("key1"));
	}
	
	@Test
	public void toSoyCompatibleMap_complexPojo_getsSimplePojo() {
		// arrange
		// act
		Map<String, ?> result = Utils.toSoyCompatibleMap(complexPojo);
		
		@SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) result.get("nestedSimplePojo");
		// assert
		
		assertEquals(nestedMap.get("stringValue"), 
		        complexPojo.getNestedSimplePojo().getStringValue());
	}
	
    @Test
    public void toSoyCompatibleMap_complexPojo_getsComplexPojo() {
        // arrange
        // act
        Map<String, ?> result = Utils.toSoyCompatibleMap(complexPojo);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> nestedComplexMap = (Map<String, Object>) result.get("nestedComplexPojo");
        
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) nestedComplexMap.get("nestedListSimple");
        
        
        // assert
        assertEquals(list.get(0), 
                complexPojo.getNestedComplexPojo().getNestedListSimple().get(0));
    }
	    

}
