package org.zoomdev.zoom.aop.reflect.javassist;

import org.junit.Test;
import org.zoomdev.zoom.aop.javassist.JavassistClassInfo;
import org.zoomdev.zoom.common.utils.CachedClasses;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class JavassistClassInfoTest {

    public static class Test1 {
        public void test(String arg0, String myArg) {

        }

        public void test1() {

        }


    }

    @Test
    public void testClassInfo() {
        JavassistClassInfo info = new JavassistClassInfo();
        Method[] methods = CachedClasses.getPublicMethods(Test1.class);
        for (Method method : methods) {

            String[] names = info.getParameterNames(Test1.class, method);
            if (method.getName().equals("test")) {
                assertEquals(names.length, 2);
                assertEquals(names[0], "arg0");
                assertEquals(names[1], "myArg");
            } else {
                assertEquals(names.length, 0);
            }
        }
    }

}
