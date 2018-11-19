package org.zoomdev.zoom.common.utils;

import junit.framework.TestCase;

public class TestStrKit extends TestCase {

    public void test(){

        assertEquals(StrKit.toUnderLine("toUnderLine"),"TO_UNDER_LINE");
        assertEquals(StrKit.toCamel("TO_UNDER_LINE"),"toUnderLine");
        assertEquals(StrKit.toCamel("_TO_UNDER_LINE"),"toUnderLine");

        assertEquals(StrKit.toCamel("TO_UNDER_LINE_"),"toUnderLine");

        assertEquals(StrKit.toCamel("TO_UNDER___LINE__"),"toUnderLine");



        assertEquals(StrKit.upperCaseFirst("testClass"),"TestClass");
        assertEquals(StrKit.upperCaseFirst("_abc"),"_abc");



    }
}
