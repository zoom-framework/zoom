package org.zoomdev.zoom.caster;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CasterTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInteger() {
		
		assertEquals(1.0, Caster.to(1, double.class));
		assertEquals("1", Caster.to(1, String.class));
		assertEquals(1.0f, Caster.to(1, float.class));
		assertEquals(true, Caster.to(1, boolean.class));
		assertEquals((byte)1, Caster.to(1, byte.class));
		assertEquals('0', Caster.to(0x30, char.class));
		assertEquals(1L, Caster.to(1, long.class));
		assertEquals((short)1, Caster.to(1, short.class));
		assertEquals(false, Caster.to(0, boolean.class));
		assertEquals(true, Caster.to(-1, boolean.class));
		assertEquals(true, Caster.to(Integer.MAX_VALUE, boolean.class));
		assertEquals(true, Caster.to(Integer.MIN_VALUE, boolean.class));
		
	}
	
	
	@Test
	public void testDouble() {
		
		assertEquals(1.0d, Caster.to(1.0d, double.class));
		assertEquals("1.0", Caster.to(1.0d, String.class));
		assertEquals(1.0f, Caster.to(1.0d, float.class));
		assertEquals(true, Caster.to(1.0d , boolean.class));
		assertEquals((byte)1, Caster.to( (double) 1.0, byte.class));
		assertEquals('0', Caster.to ( (double) 0x30, char.class));
		assertEquals(1L, Caster.to(1.0d, long.class));
		assertEquals((short)1, Caster.to(1.0d, short.class));
		
		assertEquals(false, Caster.to(0.0d, boolean.class));
		assertEquals(true, Caster.to(-1.0d, boolean.class));
		assertEquals(true, Caster.to(Double.MAX_VALUE, boolean.class));
		assertEquals(true, Caster.to(Double.MIN_VALUE, boolean.class));
		
	}
	
	
	@Test
	public void testFloat() {
		
		assertEquals(1.0d, Caster.to(1.0f, double.class));
		assertEquals("1.0", Caster.to(1.0f, String.class));
		assertEquals(1.0f, Caster.to(1.0f, float.class));
		assertEquals(true, Caster.to(1.0f , boolean.class));
		assertEquals((byte)1, Caster.to( 1.0f, byte.class));
		assertEquals('0', Caster.to ( (float) 0x30, char.class));
		assertEquals(1L, Caster.to(1.0f, long.class));
		assertEquals((short)1, Caster.to(1.0f, short.class));
		
		assertEquals(false, Caster.to(0.0f, boolean.class));
		assertEquals(true, Caster.to(-1.0f, boolean.class));
		assertEquals(true, Caster.to(Float.MAX_VALUE, boolean.class));
		assertEquals(true, Caster.to(Float.MIN_VALUE, boolean.class));
		
	}
	
	
	@Test
	public void testNull() {
		assertEquals(0.0, Caster.to(null, double.class));
		assertEquals(0, Caster.to(null, int.class));
		assertEquals(0.0f, Caster.to(null, float.class));
		assertEquals((byte)0, Caster.to(null, byte.class));
		assertEquals((short)0, Caster.to(null, short.class));
		assertEquals(0L, Caster.to(null, long.class));
		assertEquals(false, Caster.to(null, boolean.class));
		assertEquals('\0', Caster.to(null, char.class));
		
	}

	
	@Test
	public void testNull2() {
		assertEquals(null, Caster.to(null, String.class));
		assertEquals(null, Caster.to(null, Double.class));
		assertEquals(null, Caster.to(null, Integer.class));
		assertEquals(null, Caster.to(null, Float.class));
		assertEquals(null, Caster.to(null, Byte.class));
		assertEquals(null, Caster.to(null, Short.class));
		assertEquals(null, Caster.to(null, Long.class));
		assertEquals(null, Caster.to(null, Boolean.class));
		assertEquals(null, Caster.to(null, Character.class));
	}
	
	public static class TestBean{
		private String test;
	
		public String getTest() {
			return test;
		}
	
		public void setTest(String test) {
			this.test = test;
		}
	}
	
	@Test
	public void testMap() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("test", "test");
		assertEquals("{\"test\":\"test\"}", Caster.to(data, String.class));
		assertEquals(data, Caster.to("{\"test\":\"test\"}", Map.class));
		
		System.out.print( Caster.to( "{\"hello\":\"world\"}", Map.class) );
	}

    static class B {

    }

    static class A {
        private Collection<B> list;
        private String a;

        private int b;
    }


	@Test(expected = Caster.CasterException.class)
    public void testWrap() throws NoSuchFieldException {

	   assertEquals( Caster.wrap(String.class,int.class)
               .to("123"),123);
		assertEquals( Caster.wrap(String.class,int.class)
				.to("0"),0);
		assertEquals( Caster.wrap(String.class,int.class)
				.to(null),0);


        Field field = A.class.getDeclaredField("list");
        Field fa = A.class.getDeclaredField("a");
        Field fb = A.class.getDeclaredField("b");
        assertTrue(Caster.toType("[]", field.getGenericType()) instanceof List);

       ValueCaster caster=  Caster.wrapFirstVisit( fb.getGenericType() );

        assertEquals(  caster.to("1") , 1);


        caster.to(3.0f);

	}

    @Test
	public void testProvider(){
	    Caster.registerCastProvider(new Caster.CasterProvider() {
            @Override
            public ValueCaster getCaster(Class<?> srcType, Class<?> toType) {
                return null;
            }
        });


    }

    @Test
    public void testClob(){


	    assertEquals(Caster.to(new MockClob("hello"),String.class),"hello");

    }
}
