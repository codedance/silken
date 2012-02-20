/*
* (c) Copyright 1999-2012 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.papercut.silken.test;

import java.util.ArrayList;
import java.util.List;

public class SimpleArrayPojo {
	private int[] intArray = new int[10];
	private List<Integer> intList = new ArrayList<Integer>();
	
	public SimpleArrayPojo() {
		intArray = new int[] {1,2,3,4,5,6,7,8,9,10};
		
		for (int x = 0; x < 10; x++) {
			intList.add(x);
		}
	}

	public int[] getIntArray() {
		return intArray;
	}

	public void setIntArray(int[] intArray) {
		this.intArray = intArray;
	}

	public List<Integer> getIntList() {
		return intList;
	}

	public void setIntList(List<Integer> intList) {
		this.intList = intList;
	}
}
