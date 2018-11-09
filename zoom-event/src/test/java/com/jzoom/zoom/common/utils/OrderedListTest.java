package com.jzoom.zoom.common.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OrderedListTest {

	@Test
	public void test() {
		
		OrderedList<Object> list = new OrderedList<Object>();
		
		list.add(1, 0);
		list.add(2, -1);
		list.add(3, 2);
		
		
		Object[] objects = list.toArray(new Object[list.size()]);
		
		assertEquals(objects[0], 2);
		assertEquals(objects[1], 1);
		assertEquals(objects[2], 3);
		
	}
}
