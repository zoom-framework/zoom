package org.zoomdev.zoom.ioc.modules;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.config.ConfigReader;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.IocClassLoader;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.IocMethod;
import org.zoomdev.zoom.ioc.configuration.SimpleConfigBuilder;
import org.zoomdev.zoom.ioc.impl.ZoomIocContainer;
import org.zoomdev.zoom.ioc.models.PushService;
import org.zoomdev.zoom.ioc.models.ShopService;

import java.io.IOException;

public class TestIoc extends TestCase {


    public void test() throws IOException {
        IocContainer ioc = new ZoomIocContainer(
        );


        IocClassLoader classLoader = ioc.getIocClassLoader();

        ioc.getIocClassLoader().appendModule(ShopModule.class);

        ioc.waitFor();

        ShopService shopService = ioc.fetch(ShopService.class);

        PushService pushService = ioc.fetch(PushService.class);

        assertEquals(pushService.getName(), shopService.getName());


        shopService.showProduct();

        shopService.editProduct("test");

        assertEquals(shopService.showProduct(), "test");


        IocContainer subIoc = new ZoomIocContainer(
                ioc
        );


        Classes.destroy(ioc);

        Classes.destroy(subIoc);
    }


    public static class A{

        private final B b;

        public A(B b){
            this.b = b;
        }
        
    }

    public static class B{

    }

    public static class C{

    }



    public void testScopeIoc() throws IOException {

        IocContainer ioc = new ZoomIocContainer(
        );

        ioc.addEventListener(new IocMethodVisitorImpl());



        IocContainer container = new ZoomIocContainer(
                ioc
        );

        container.getIocClassLoader().append(A.class);

        container.getIocClassLoader().append(B.class);


        container.getIocClassLoader().append(C.class);


        container.fetch(A.class);
        container.fetch(B.class);
        container.fetch(C.class);



        Classes.destroy(container);

    }
}
