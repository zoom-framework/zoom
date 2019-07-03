package org.zoomdev.zoom.timer.impl;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.aop.modules.AopModule;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.impl.ZoomIocContainer;
import org.zoomdev.zoom.ioc.modules.IocModule;
import org.zoomdev.zoom.timer.annotation.Timer;
import org.zoomdev.zoom.timer.modules.TimerModule;

import java.io.IOException;


public class TestTimerModule extends TestCase {
    public static class TestService {

        @Timer(every = Timer.Every.SECOND, everyValue = 1)
        public void test(IocContainer container) {

            System.out.println("timer");
        }

        @Timer(every = Timer.Every.HOUR, everyValue = 1)
        public void test1(IocContainer container) {

            System.out.println("timer");
        }

        @Timer(every = Timer.Every.MINUTE, everyValue = 1)
        public void test2(IocContainer container) {

            System.out.println("timer");
        }
    }

    @Module
    public static class TestModule {

        @Inject
        public void inject(TestService service) {

        }

        @IocBean
        public TestService getTestService() {
            return new TestService();
        }

    }

    private static final Log log = LogFactory.getLog(TestTimerModule.class);

    public void test() throws IOException, InterruptedException {
        IocContainer container = new ZoomIocContainer();

        container.getIocClassLoader().appendModule(IocModule.class);
        container.getIocClassLoader().appendModule(AopModule.class);
        container.getIocClassLoader().appendModule(TimerModule.class);


        container.fetch(IocModule.class);
        container.fetch(AopModule.class);
        container.fetch(TimerModule.class);


        IocContainer subContainer = new ZoomIocContainer(
                container, container.getClassLoader()
        );


        subContainer.getIocClassLoader().appendModule(TestModule.class);
        subContainer.fetch(TestModule.class);


        Thread.sleep(2000);


        Classes.destroy(subContainer);


        Thread.sleep(2000);

        Classes.destroy(container);

    }
}
