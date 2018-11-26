package org.zoomdev.zoom.cache;

import junit.framework.TestCase;
import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.aop.javassist.JavassistAopFactory;
import org.zoomdev.zoom.cache.annotations.Cache;
import org.zoomdev.zoom.cache.annotations.CacheRemove;
import org.zoomdev.zoom.cache.ehcache.EhDataCache;
import org.zoomdev.zoom.cache.modules.CacheModule;
import org.zoomdev.zoom.common.filter.MethodFilter;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.CachedClasses;

import java.io.IOException;
import java.lang.reflect.Method;

public class TestCache extends TestCase {

    public static class TestCacheService{


        private int count;

        @CacheRemove(format = "test%s")
        public void remove(String key){

        }


        @Cache(format = "test%s")
        public String get(String key){
            ++count;
            return "MyTestValue";
        }

        // if cache is null return null
        @Cache(format = "test_not_fill%s", timeoutMs = 3000,ignoreNull = false,fill = false)
        public String getButNotFill(String key){
            ++count;
            return "TestValue";
        }



        @Cache(format = "getWithoutFormat")
        public String getWithoutFormat(){
            ++count;
            return "TestWithoutFormat";
        }

    }




    public void test() throws IllegalAccessException, InstantiationException, IOException {

        final AopFactory aopFactory = new JavassistAopFactory();


        ResScanner resScanner = new ResScanner();
        resScanner.scan();

        CacheModule cacheModule = new CacheModule();
        EhDataCache cache = (EhDataCache) cacheModule.getCache();
        cache.init(resScanner);

        cacheModule.init(aopFactory, cache);

        System.out.println("======2=");
        //test cache

        cache.set("test", "value", 1000);
        System.out.println("======3=");

        assertEquals(cache.get("test"), "value");
        System.out.println("======4=");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("======5=");
        assertEquals(cache.get("test"), "value");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(cache.get("test"), null);
        System.out.println("======6=");


        TestCacheService service = (TestCacheService) aopFactory.enhance(TestCacheService.class).newInstance();

        int count = service.count;

        String value = service.get("mykey");
        assertEquals("MyTestValue",value);

        //=====
        value = service.get("mykey");
        assertEquals(count+1,service.count);
        assertEquals("MyTestValue",value);

        // remove
        service.remove("mykey");


        ////
        String newValue = service.getButNotFill("mykey");
        assertEquals("TestValue",newValue);


        service.getButNotFill("testKey");
        count = service.count;

        service.getButNotFill("testKey");
        assertEquals(count+1,service.count);


        Method method = CachedClasses.getPublicMethod(service.getClass(), new MethodFilter() {
            @Override
            public boolean accept(Method value) {
                return value.getName().startsWith("getWithoutFormat");
            }
        });
        count = service.count;
        String v = service.getWithoutFormat();

        service.getWithoutFormat();

        assertEquals(count+1,service.count);

        assertEquals("TestWithoutFormat",v);


        cache.set("test","value");

        assertEquals(cache.get("test"),"value");

        cache.remove("test");
        assertEquals(cache.get("test"),null);
    }
}
