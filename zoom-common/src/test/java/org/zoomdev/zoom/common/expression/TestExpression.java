package org.zoomdev.zoom.common.expression;

import junit.framework.TestCase;

public class TestExpression extends TestCase {

    public void test(){
        Symbol symbol = Symbol.parse("<>");
        assertNotNull(symbol);
        assertTrue(symbol.compare(1,2));
        assertFalse(symbol.compare(1,1));
        assertEquals(symbol.value(),"<>");


        symbol = Symbol.parse("=");
        assertNotNull(symbol);
        assertFalse(symbol.compare(1,2));
        assertTrue(symbol.compare(1,1));
        assertEquals(symbol.value(),"=");

        symbol = Symbol.parse(">");
        assertNotNull(symbol);
        assertFalse(symbol.compare(1,2));
        assertFalse(symbol.compare(1,1));
        assertTrue(symbol.compare(2,1));
        assertEquals(symbol.value(),">");

        symbol = Symbol.parse("<");
        assertNotNull(symbol);
        assertTrue(symbol.compare(1,2));
        assertFalse(symbol.compare(1,1));
        assertFalse(symbol.compare(2,1));
        assertEquals(symbol.value(),"<");


        symbol = Symbol.parse(">=");
        assertNotNull(symbol);
        assertFalse(symbol.compare(1,2));
        assertTrue(symbol.compare(1,1));
        assertTrue(symbol.compare(2,1));
        assertEquals(symbol.value(),">=");


        symbol = Symbol.parse("<=");
        assertNotNull(symbol);
        assertTrue(symbol.compare(1,2));
        assertTrue(symbol.compare(1,1));
        assertFalse(symbol.compare(2,1));
        assertEquals(symbol.value(),"<=");

        symbol = Symbol.parse("> =");
        assertNull(symbol);


    }
}
