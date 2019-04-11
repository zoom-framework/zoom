package org.zoomdev.zoom.http.utils;

import org.junit.Test;
import org.zoomdev.zoom.http.Destroyable;
import org.zoomdev.zoom.http.exceptions.ZoomException;
import org.zoomdev.zoom.http.filter.MethodFilter;

import java.io.IOException;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.sql.Time;
import java.util.*;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertNull;

public class ClassesUtilTest {


    enum TestEnum {
        Test,
        Hello
    }

    private List<String> list;


    public static class A {

        public static String name;

        public void start() {

        }
    }


    public static class B implements Destroyable {
        String name;

        @Override
        public void destroy() {
            name = null;
        }
    }

    @Test
    public void test() throws Exception {

        Field field = Classes.getField(ClassesUtilTest.class, "list");
        assertNotNull(field);
        assertNull(Classes.getField(ClassesUtilTest.class, "list1"));

        assertEquals(Classes.getClass(field.getGenericType()), List.class);
        assertEquals(Classes.getClass(Integer.class), Integer.class);


        // Classes.newInstance(Integer.class);


        Classes.newInstance(A.class);

        Constructor constructor = Classes.findNoneParameterConstructor(A.class);
        assertNotNull(constructor);


        assertTrue(Classes.hasInterface(Integer.class, Comparable.class));

        assertFalse(Classes.hasInterface(A.class, Destroyable.class));


        CachedClasses.getPublicMethods(A.class);
        CachedClasses.getFields(A.class);
        CachedClasses.getPublicMethods(A.class, "*");


        CachedClasses.getPublicMethods(A.class, new MethodFilter() {
            @Override
            public boolean accept(Method value) {
                return false;
            }
        });


        CachedClasses.getPublicMethod(A.class, new MethodFilter() {
            @Override
            public boolean accept(Method value) {
                return value.getName().startsWith("start");
            }
        });


        B b = new B();
        b.name = "test";

        Classes.destroy(this);
        Classes.destroy(b);
        assertNull(b.name);


        B[] bs = new B[2];
        B b0, b1;
        bs[0] = b0 = new B();
        bs[1] = b1 = new B();
        bs[0].name = "b0";
        bs[1].name = "b1";

        Classes.destroy(bs);

        assertNull(b0.name);
        assertNull(b1.name);
        assertNull(bs[0]);
        assertNull(bs[1]);

        b0.name = "b0";
        b1.name = "b1";
        List<B> bList = CollectionUtils.asList(b0, b1);
        Classes.destroy(bList);
        assertNull(b0.name);
        assertNull(b1.name);

        assertEquals(bList.size(), 0);


        b0.name = "b0";
        b1.name = "b1";
        Map<String, B> map = new HashMap<String, B>();
        map.put("1", b0);
        map.put("2", b1);
        Classes.destroy(map);
        assertNull(b0.name);
        assertNull(b1.name);

        assertEquals(map.size(), 0);


        Classes.forName("java.lang.Integer");

        try {
            Classes.forName("tst");
        } catch (Exception e) {
            String str = Classes.formatStackTrace(e);
            System.out.println(str);
        }


        Method method = CachedClasses.getPublicMethod(A.class, new MethodFilter() {
            @Override
            public boolean accept(Method value) {
                return value.getName().startsWith("start");
            }
        });

        assertNotNull(method);

        assertEquals(Classes.getParameterCount(method), 0);

        Classes.set(A.class, "name", "A");


        assertTrue(Classes.isEqual(A.class, A.class));

        assertFalse(Classes.isEqual(null, A.class));


        assertTrue(Classes.isSimple(Date.class));

        assertTrue(Classes.isSimple(Integer.class));
        assertTrue(Classes.isSimple(BigInteger.class));
        assertTrue(!Classes.isSimple(A.class));
        assertTrue(Classes.isSimple(String.class));
        assertTrue(Classes.isSimple(int.class));

        assertTrue(Classes.isBoolean(Boolean.class));

        assertTrue(Classes.isEnum(
                TestEnum.class
        ));

        assertTrue(Classes.isChar(char.class));
        assertTrue(Classes.isChar(Character.class));
        assertTrue(!Classes.isChar(A.class));


        assertTrue(Classes.isDateTime(Date.class));
        assertTrue(Classes.isDateTime(Calendar.class));
        assertTrue(Classes.isDateTime(Time.class));
        assertTrue(Classes.isDateTime(java.sql.Date.class));

        assertTrue(!Classes.isDateTime(A.class));

        assertTrue(Classes.isNumber(Float.class));
        assertTrue(!Classes.isNumber(Boolean.class));

        assertTrue(Classes.isString("test".getClass()));

        assertTrue(!Classes.isWapClass(Integer.class, int.class));
        assertTrue(!Classes.isWapClass(float.class, Integer.class));
        assertTrue(!Classes.isWapClass(Integer.class, float.class));
        assertTrue(Classes.isWapClass(int.class, Integer.class));


        Type[] types = Classes.getAllParameterizedTypes(DataObject.class);
        assertEquals(types.length, 2);
        assertEquals(types[0], String.class);
        assertEquals(types[1], Object.class);


        CachedClasses.clear();
    }

    @Test(expected = ZoomException.class)
    public void testFetchField() {
        Field field = Classes.fetchField(ClassesUtilTest.class, "list");
        assertNotNull(field);
        assertNull(Classes.fetchField(ClassesUtilTest.class, "list1"));
    }


    @Test(expected = RuntimeException.class)
    public void restMakeThrowable() {
        throw Classes.makeThrow(new InvocationTargetException(
                new IOException()
        ));
    }

    @Test(expected = RuntimeException.class)
    public void restMakeThrowable1() {
        throw Classes.makeThrow(
                new IOException());
    }


    @Test(expected = ZoomException.class)
    public void restMakeThrowable2() {
        throw Classes.makeThrow(new ZoomException());
    }


}
