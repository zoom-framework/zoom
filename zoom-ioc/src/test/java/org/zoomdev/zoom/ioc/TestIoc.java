package org.zoomdev.zoom.ioc;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.config.ConfigReader;
import org.zoomdev.zoom.common.res.ClassResolvers;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.Classes;
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
        ResScanner scanner = ResScanner.me();
        scanner.scan();


        ConfigReader.getDefault().load(scanner.getFile("app.properties").getFile());

        ClassResolvers resolvers = new ClassResolvers(
                new SimpleConfigBuilder(ioc)
        );
        resolvers.visit(scanner);


        ShopService shopService = ioc.fetch(ShopService.class);

        PushService pushService = ioc.fetch(PushService.class);

        assertEquals(pushService.getName(), shopService.getName());


        shopService.showProduct();

        shopService.editProduct("test");

        assertEquals(shopService.showProduct(), "test");


        IocContainer subIoc = new ZoomIocContainer(
                ioc.getScope(),
                ioc.getIocClassLoader(),
                ioc.getEventListeners()
        );


        Classes.destroy(ioc);

        Classes.destroy(subIoc);
    }
}
