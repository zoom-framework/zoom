package org.zoomdev.zoom.web;

import junit.framework.TestCase;

public class TestAop extends TestCase {
    public void test() {

    }
//
//
//    @Test(expected = TestException.class)
//    public void testEnhanceClassOfQAopConfigArray() throws Exception {
//
//        new TestReplaceArg();
//        new TestReplaceReturn();
//        AopFactory factory = new JavassistAopFactory(new TestAopMaker(), new AopMethodInterceptorFactory());
//
//        Class<?> modelClass = factory.enhance(TestModel.class);
//        TestModel model = (TestModel) modelClass.newInstance();
//        model.testVoid();
//        assertEquals(model.add(1, 2), 3);
//        model.testMap();
//        model.testList(new ArrayList<Map<String, Object>>());
//
//        assertEquals(model.testArgs(""), "Replaced");
//        assertEquals(model.testArgsAll(""), "Replaced");
//        assertEquals(model.testReturn(), "ReplaceResult");
//
//        model.testException();
//
//
//    }
//
//
//    @Test(expected = InvalidParameterException.class)
//    public void testArgs() throws InstantiationException, IllegalAccessException {
//        AopFactory factory = new JavassistAopFactory(new TestAopMaker(), new AopMethodInterceptorFactory());
//
//        Class<?> modelClass = factory.enhance(TestModel.class);
//        TestModel model = (TestModel) modelClass.newInstance();
//        model.testExceptionArgsLength("", "");
//    }

}
