package com.jzoom.zoom.common.json;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class JSONTest{

	@Test
	public void test() {
		
		JSON.stringify(new HashMap());
		JSON.parse("{}", Map.class);
	}
}
