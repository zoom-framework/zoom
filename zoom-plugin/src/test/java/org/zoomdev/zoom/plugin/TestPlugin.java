package org.zoomdev.zoom.plugin;

import junit.framework.TestCase;
import org.junit.Test;
import org.zoomdev.zoom.aop.javassist.JavassistClassInfo;
import org.zoomdev.zoom.aop.reflect.ClassInfo;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.impl.ZoomIocContainer;
import org.zoomdev.zoom.plugin.impl.ZoomPluginHost;
import org.zoomdev.zoom.web.action.impl.SimpleActionFactory;
import org.zoomdev.zoom.web.modules.WebModules;
import org.zoomdev.zoom.web.router.Router;
import org.zoomdev.zoom.web.router.impl.ZoomRouter;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class TestPlugin {


    @Test
    public void test() throws IOException, PluginException {
        IocContainer iocContainer = new ZoomIocContainer();

        ClassInfo info = new JavassistClassInfo();
        iocContainer.getIocClassLoader().append(ClassInfo.class,info,true);

        iocContainer.getIocClassLoader().appendModule(WebModules.class);

        SimpleActionFactory simpleActionFactory = new SimpleActionFactory();
        iocContainer.getIocClassLoader().append(SimpleActionFactory.class,simpleActionFactory,true);

        Router router = new ZoomRouter();
        PluginHost host = new ZoomPluginHost(
            iocContainer,router
        );



        ResScanner scanner = new ResScanner();
        scanner.scan();

        List<ResScanner.Res> list= scanner.findFile("*demo*.jar");
        assertTrue(list.size()>0);
        ResScanner.Res res = list.get(0);
        PluginHolder holder = host.load(new URL("file://"+res.getFile().getAbsolutePath()));

        host.install(holder);

        host.startup(holder);

        host.shutdown(holder,true);


        host.uninstall(holder);


        Classes.destroy(host);

    }

    @Test
    public void testHostStartupShutdown() throws IOException, PluginException {
        IocContainer iocContainer = new ZoomIocContainer();

        ClassInfo info = new JavassistClassInfo();
        iocContainer.getIocClassLoader().append(ClassInfo.class,info,true);

        iocContainer.getIocClassLoader().appendModule(WebModules.class);

        SimpleActionFactory simpleActionFactory = new SimpleActionFactory();
        iocContainer.getIocClassLoader().append(SimpleActionFactory.class,simpleActionFactory,true);

        Router router = new ZoomRouter();
        PluginHost host = new ZoomPluginHost(
                iocContainer,router
        );



        ResScanner scanner = new ResScanner();
        scanner.scan();

        List<ResScanner.Res> list= scanner.findFile("*demo*.jar");
        assertTrue(list.size()>0);
        ResScanner.Res res = list.get(0);
        PluginHolder holder = host.load(new URL("file://"+res.getFile().getAbsolutePath()));



        host.startup();


        host.shutdown(true);

        PluginHolder pluginHolder = host.getPluginById("demo");
        assertNotNull(pluginHolder);


        assertEquals(pluginHolder.getVersion(),"1.0");

        assertEquals(pluginHolder.getDescription(),"Demo 插件");
        assertEquals(pluginHolder.getTitle(),"DemoPlugin");

        assertEquals(pluginHolder.getUid(),"demo");

        Classes.destroy(host);

    }




    @Test(expected = PluginException.class)
    public void testException() throws IOException, PluginException {
        IocContainer iocContainer = new ZoomIocContainer();

        ClassInfo info = new JavassistClassInfo();
        iocContainer.getIocClassLoader().append(ClassInfo.class,info,true);

        iocContainer.getIocClassLoader().appendModule(WebModules.class);

        SimpleActionFactory simpleActionFactory = new SimpleActionFactory();
        iocContainer.getIocClassLoader().append(SimpleActionFactory.class,simpleActionFactory,true);

        Router router = new ZoomRouter();
        PluginHost host = new ZoomPluginHost(
                iocContainer,router
        );



        ResScanner scanner = new ResScanner();
        scanner.scan();

        List<ResScanner.Res> list= scanner.findFile("*.class");
        assertTrue(list.size()>0);
        ResScanner.Res res = list.get(0);
        PluginHolder holder = host.load(new URL("file://"+res.getFile().getAbsolutePath()));


    }
}
