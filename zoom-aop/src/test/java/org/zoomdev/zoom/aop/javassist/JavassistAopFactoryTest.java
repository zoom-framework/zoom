package org.zoomdev.zoom.aop.javassist;

import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.MethodInvoker;
import org.zoomdev.zoom.aop.annotations.Aop;
import org.zoomdev.zoom.aop.annotations.Log;
import org.zoomdev.zoom.aop.factory.AnnotationMethodInterceptorFactory;
import org.zoomdev.zoom.aop.factory.AopMethodInterceptorFactory;
import org.zoomdev.zoom.aop.factory.LogMethodInterceptorFactory;
import org.zoomdev.zoom.aop.interceptors.LogMethodCallback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.*;

import static org.junit.Assert.*;

public class JavassistAopFactoryTest {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface TestAnnonation {


    }


    public static class TestException extends Exception {

        public TestException(String string) {
            super(string);
        }

    }

    public static class TestModel {
        @TestAnnonation
        public void testVoid() {
            System.out.println("testVoid");
        }

        @Log
        @TestAnnonation
        public int add(int a, int b) {
            return a + b;
        }

        @Log
        public void testException() throws Exception {
            throw new TestException("This is a Exception");
        }

        public Map<String, Object> testMap() {
            return new HashMap<String, Object>();
        }

        public List<Map<String, Object>> testList(List<Map<String, Object>> list) {
            return new ArrayList<Map<String, Object>>();
        }

        @Aop(value = TestReplaceArg.class)
        public String testArgs(String arg) {
            return arg;
        }

        @Aop(value = TestReplaceReturn.class)
        public String testReturn() {
            return "";
        }


        @Aop(value = TestReplaceArgsAll.class)
        public String testArgsAll(String arg) {
            return arg;
        }


        @Aop(value = TestReplaceArgsAll.class)
        public String testExceptionArgsLength(String arg, String arg2) {
            return arg;
        }
    }

    public static class TestReplaceArg implements MethodInterceptor {

        @Override
        public void intercept(MethodInvoker invoker) throws Throwable {
            invoker.setArg(0, "Replaced");
            invoker.invoke();
        }

    }

    public static class TestReplaceReturn implements MethodInterceptor {

        @Override
        public void intercept(MethodInvoker invoker) throws Throwable {
            invoker.setReturnObject("ReplaceResult");
            invoker.invoke();
        }
    }


    public static class TestReplaceArgsAll implements MethodInterceptor {
        @Override
        public void intercept(MethodInvoker invoker) throws Throwable {
            invoker.setArgs(new Object[]{"Replaced"});
            invoker.invoke();
        }
    }

    public class TestAopMaker extends AnnotationMethodInterceptorFactory<TestAnnonation> {

        @Override
        protected void createMethodInterceptors(TestAnnonation annotation, Method method, List<MethodInterceptor> interceptors) {
            interceptors.add(new MethodInterceptor() {

                @Override
                public void intercept(MethodInvoker invoker) throws Throwable {
                    System.out.print("我是打酱油的\n");
                    invoker.invoke();
                }
            });
        }

    }


    @Test(expected = TestException.class)
    public void testEnhanceClassOfQAopConfigArray() throws Exception {

        new TestReplaceArg();
        new TestReplaceReturn();
        AopFactory factory = new JavassistAopFactory(new TestAopMaker(), new LogMethodInterceptorFactory(), new AopMethodInterceptorFactory());

        Class<?> modelClass = factory.enhance(TestModel.class);
        TestModel model = (TestModel) modelClass.newInstance();
        model.testVoid();
        assertEquals(model.add(1, 2), 3);
        model.testMap();
        model.testList(new ArrayList<Map<String, Object>>());

        assertEquals(model.testArgs(""), "Replaced");
        assertEquals(model.testArgsAll(""), "Replaced");
        assertEquals(model.testReturn(), "ReplaceResult");

        model.testException();


    }


    @Test(expected = InvalidParameterException.class)
    public void testArgs() throws InstantiationException, IllegalAccessException {
        AopFactory factory = new JavassistAopFactory(new TestAopMaker(), new LogMethodInterceptorFactory(), new AopMethodInterceptorFactory());

        Class<?> modelClass = factory.enhance(TestModel.class);
        TestModel model = (TestModel) modelClass.newInstance();
        model.testExceptionArgsLength("", "");
    }


    @Test()
    public void testJavassistAopFactory() {
        JavassistAopFactory factory = new JavassistAopFactory();
        assertNotNull(factory);
        factory.destroy();
    }


    public static class A {

        public void test() {

        }

        public int testInt() {
            return 0;
        }

        public float testFloat() {
            return 0f;
        }

        public double testDouble() {
            return 0d;
        }


        public String testString() {
            return "test";
        }

        public void testException() throws Exception {
            throw new Exception();
        }
    }

    private static final org.apache.commons.logging.Log log = LogFactory.getLog(JavassistAopFactoryTest.class);

    @Test(expected = Exception.class)
    public void testAddMaker() throws Exception {
        AopFactory factory = new JavassistAopFactory();
        factory.addFilter(new LogMethodCallback(), "*", 0);

        factory.addFilter(new MethodInterceptor() {

            @Override
            public void intercept(MethodInvoker invoker) throws Throwable {
                log.info("before invoke method:" + invoker.getMethod());
                try {
                    invoker.invoke();
                } catch (Throwable e) {
                    log.error("When error invoke method:" + invoker.getMethod());
                    throw e;
                } finally {
                    log.error("When complete invoke method:" + invoker.getMethod());
                }

            }
        }, "*", 0);        //The LogMethodCallback will apply to every public method enhanced by the AopFactory


        final Set<String> invokedMethods = new HashSet<String>();
        factory.addFilter(new MethodInterceptor() {

            @Override
            public void intercept(MethodInvoker invoker) throws Throwable {

                invokedMethods.add(invoker.getMethod().getName());
                assertEquals(invoker.isInvoked(), false);
                invoker.invoke();
                Object result = invoker.getReturnObject();
                assertEquals("test", result);

                assertEquals(invoker.isInvoked(), true);

            }
        }, "*A#*String", 1);


        Class<?> clazz = factory.enhance(A.class);
        assertTrue(A.class.isAssignableFrom(clazz));
        A a = (A) clazz.newInstance();

        a.test();

        assertEquals("test", a.testString());

        assertEquals((Double) (double) 0, (Double) a.testDouble());
        assertTrue(a.testFloat() == 0f);

        assertEquals(0, a.testInt());

        assertTrue(invokedMethods.contains("testString"));
        assertTrue(!invokedMethods.contains("testInt"));
        assertTrue(!invokedMethods.contains("testFloat"));
        assertTrue(!invokedMethods.contains("testDouble"));
        assertTrue(!invokedMethods.contains("testException"));


        try {
            a.testException();
        } finally {
            ((JavassistAopFactory) factory).destroy();
        }


    }


}
