package org.zoomdev.zoom.timer.impl;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.res.ClassResolvers;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.IocConstructor;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.configuration.SimpleConfigBuilder;
import org.zoomdev.zoom.ioc.impl.SimpleIocContainer;
import org.zoomdev.zoom.timer.impl.modules.TestModule;
import org.zoomdev.zoom.timer.modules.TimerModule;

import java.io.IOException;

public class TestTimerModule extends TestCase {

    public void test() throws IOException {


        IocContainer ioc = new SimpleIocContainer();
        ClassResolvers classResolvers = new ClassResolvers(
                new SimpleConfigBuilder(ioc)
        );
        ResScanner scanner = ResScanner.me();
        scanner.scan();
        classResolvers.visit(scanner);


        TestModule.TestService testModule = ioc.get(TestModule.TestService.class);


        Classes.destroy(ioc);


    }
}
