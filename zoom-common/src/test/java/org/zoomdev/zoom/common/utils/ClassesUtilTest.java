package org.zoomdev.zoom.common.utils;

import junit.framework.TestCase;
import org.junit.Test;
import org.zoomdev.zoom.common.exceptions.ZoomException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ClassesUtilTest {

    private List<String> list;

    @Test
    public void test() {

        Field field = Classes.getField(ClassesUtilTest.class, "list");
        assertNotNull(field);
        assertNull(Classes.getField(ClassesUtilTest.class, "list1"));

       assertEquals(Classes.getClass(field.getGenericType()),List.class);
       assertEquals(Classes.getClass(Integer.class),Integer.class);


       Classes.newInstance(Integer.class);



    }

    @Test(expected = ZoomException.class)
    public void testFetchField() {
        Field field = Classes.fetchField(ClassesUtilTest.class, "list");
        assertNotNull(field);
        assertNull(Classes.fetchField(ClassesUtilTest.class, "list1"));
    }


    @Test(expected = RuntimeException.class)
    public void restMakeThrowable(){
        Classes.makeThrow(new InvocationTargetException(
                new IOException()
        ));
    }

    @Test(expected = RuntimeException.class)
    public void restMakeThrowable1(){
        Classes.makeThrow(
                new IOException());
    }


    @Test(expected = ZoomException.class)
    public void restMakeThrowable2(){
        Classes.makeThrow(new ZoomException());
    }


}
