package com.jzoom.zoom.common.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ValidateUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testVariable() {
		assertTrue(	ValidUtils.isJavaVariableName("$c123"));
		assertTrue(	ValidUtils.isJavaVariableName("_a$c123"));
		assertTrue(	ValidUtils.isJavaVariableName("a$cA123"));
		assertFalse(	ValidUtils.isJavaVariableName("a$c123."));
		assertFalse(	ValidUtils.isJavaVariableName("$c123."));
		assertFalse(	ValidUtils.isJavaVariableName("a$c123-"));
		assertFalse(	ValidUtils.isJavaVariableName("1a$c123"));
		
		assertFalse(	ValidUtils.isJavaVariableName(""));
		assertFalse(	ValidUtils.isJavaVariableName(null));
	}
	
	@Test
	public void testClassName() {
		assertTrue(	ValidUtils.isJavaClassName("$c123"));
		assertTrue(	ValidUtils.isJavaClassName("_a$c123"));
		assertTrue(	ValidUtils.isJavaClassName("a$cA123"));
		assertFalse(	ValidUtils.isJavaClassName("a$c123."));
		assertFalse(	ValidUtils.isJavaClassName("$c123."));
		assertFalse(	ValidUtils.isJavaClassName("a$c123-"));
		assertFalse(	ValidUtils.isJavaClassName("1a$c123"));
		
		
		
		assertTrue(	ValidUtils.isJavaClassName("$c123._test123"));
		assertFalse(	ValidUtils.isJavaClassName("$c123._test123."));
		assertFalse(	ValidUtils.isJavaClassName(".$c123._test123"));
		
		assertFalse(	ValidUtils.isJavaClassName(""));
		assertFalse(	ValidUtils.isJavaClassName(null));
	}

}
