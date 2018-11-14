package org.zoomdev.zoom.common.utils;

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
        assertTrue(ValidateUtils.isJavaVariableName("$c123"));
        assertTrue(ValidateUtils.isJavaVariableName("_a$c123"));
        assertTrue(ValidateUtils.isJavaVariableName("a$cA123"));
        assertFalse(ValidateUtils.isJavaVariableName("a$c123."));
        assertFalse(ValidateUtils.isJavaVariableName("$c123."));
        assertFalse(ValidateUtils.isJavaVariableName("a$c123-"));
        assertFalse(ValidateUtils.isJavaVariableName("1a$c123"));

        assertFalse(ValidateUtils.isJavaVariableName(""));
        assertFalse(ValidateUtils.isJavaVariableName(null));
    }

    @Test
    public void testClassName() {
        assertTrue(ValidateUtils.isJavaClassName("$c123"));
        assertTrue(ValidateUtils.isJavaClassName("_a$c123"));
        assertTrue(ValidateUtils.isJavaClassName("a$cA123"));
        assertFalse(ValidateUtils.isJavaClassName("a$c123."));
        assertFalse(ValidateUtils.isJavaClassName("$c123."));
        assertFalse(ValidateUtils.isJavaClassName("a$c123-"));
        assertFalse(ValidateUtils.isJavaClassName("1a$c123"));


        assertTrue(ValidateUtils.isJavaClassName("$c123._test123"));
        assertFalse(ValidateUtils.isJavaClassName("$c123._test123."));
        assertFalse(ValidateUtils.isJavaClassName(".$c123._test123"));

        assertFalse(ValidateUtils.isJavaClassName(""));
        assertFalse(ValidateUtils.isJavaClassName(null));
    }

}
