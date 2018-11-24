package org.zoomdev.zoom.cache;

import junit.framework.TestCase;
import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.aop.javassist.JavassistAopFactory;
import org.zoomdev.zoom.cache.modules.CacheModule;

public class TestCache extends TestCase {


    public void test(){

        AopFactory aopFactory = new JavassistAopFactory();

        CacheModule cacheModule = new CacheModule();
        DataCache cache = cacheModule.getCache();
        cacheModule.init(aopFactory,cache);

        //test cache





    }
}
