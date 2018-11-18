package org.zoomdev.zoom.dao.impl.module;


import org.junit.Test;
import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.aop.javassist.JavassistAopFactory;
import org.zoomdev.zoom.dao.impl.ZoomDao;
import org.zoomdev.zoom.dao.modules.DaoModule;
import org.zoomdev.zoom.dao.transaction.Trans;

import static org.junit.Assert.assertTrue;

public class TestModule {

    public static class TransTest {

        @Trans
        public void testTrans() {
            System.out.println("trans");

            assertTrue(ZoomDao.getTransaction() != null);
        }
    }

    @Test
    public void test() throws IllegalAccessException, InstantiationException {

        AopFactory factory = new JavassistAopFactory();

        DaoModule module = new DaoModule();
        module.config(factory);

        TransTest test = (TransTest) factory.enhance(TransTest.class).newInstance();

        test.testTrans();


    }

}
