package org.zoomdev.zoom.timer.impl;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.res.ClassResolvers;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.IocConstructor;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.configuration.SimpleConfigBuilder;
import org.zoomdev.zoom.ioc.impl.SimpleIocContainer;
import org.zoomdev.zoom.ioc.modules.IocModule;
import org.zoomdev.zoom.timer.impl.modules.TestModule;
import org.zoomdev.zoom.timer.modules.TimerModule;

import java.io.IOException;

public class TestTimerModule extends TestCase {

    private static final Log log = LogFactory.getLog(TestTimerModule.class);

    public void test() throws IOException {
        log.info("=====================timer =================");
        log.info("=====================timer =================");
        log.info("=====================timer =================");
        log.info("=====================timer =================");
        log.info("=====================timer =================");

        IocContainer ioc = new SimpleIocContainer();
        ioc.getIocClassLoader().appendModule(IocModule.class);
        ioc.get(IocModule.class);
        
        ClassResolvers classResolvers = new ClassResolvers(
                new SimpleConfigBuilder(ioc)
        );
        ResScanner scanner = ResScanner.me();
        scanner.scan();
        classResolvers.visit(scanner);
        //这里如果是maven的测试，可能会扫描不到目录

       

       


        assertEquals(ioc.get(IocModule.class).getClass(),IocModule.class);

        TestModule.TestService testModule = ioc.get(TestModule.TestService.class);


        Classes.destroy(ioc);


    }
}
