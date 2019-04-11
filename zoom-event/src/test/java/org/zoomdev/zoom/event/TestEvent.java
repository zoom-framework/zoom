package org.zoomdev.zoom.event;

import junit.framework.TestCase;
import org.apache.commons.lang3.ObjectUtils;
import org.zoomdev.zoom.aop.modules.AopModule;
import org.zoomdev.zoom.http.annotations.Inject;
import org.zoomdev.zoom.http.annotations.Module;
import org.zoomdev.zoom.event.annotations.EventNotifier;
import org.zoomdev.zoom.event.annotations.EventObserver;
import org.zoomdev.zoom.event.modules.EventModule;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.impl.ZoomIocContainer;
import org.zoomdev.zoom.ioc.modules.IocModule;

import java.util.concurrent.atomic.AtomicInteger;

public class TestEvent extends TestCase {

    @Module
    public static class TestEventModule {


        public TestEventObserver getObserver() {
            return observer;
        }

        public void setObserver(TestEventObserver observer) {
            this.observer = observer;
        }

        public TestEventNotifier getNotifier() {
            return notifier;
        }

        public void setNotifier(TestEventNotifier notifier) {
            this.notifier = notifier;
        }

        @Inject
        private TestEventObserver observer;

        @Inject
        private TestEventNotifier notifier;


    }

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static class TestEventObserver {

        @EventObserver("onSuccess")
        public void onEvent(Event event) {
            counter.incrementAndGet();
            assertTrue(ObjectUtils.equals(event.getData(), "success")
                    || ObjectUtils.equals(event.getData(), null));
        }

        @EventObserver("onSuccess")
        public void onEvent1(String name, Object event) {
            counter.incrementAndGet();
            assertTrue(ObjectUtils.equals(event, "success")
                    || ObjectUtils.equals(event, null));
        }

        @EventObserver("onSuccess")
        public void onEvent2(String name) {
            counter.incrementAndGet();

        }

        @EventObserver("onSuccess")
        public void onEvent3(String name, Object event, Exception error) {
            counter.incrementAndGet();

        }

        @EventObserver("onSuccess")
        public void onEvent() {
            counter.incrementAndGet();
        }
    }

    public static class TestEventNotifier {


        @EventNotifier("onSuccess")
        public Object notifySuccess(Object successData) {
            return successData;
        }

        @EventNotifier("onSuccess")
        public Object notifySuccess1(Object successData) throws Exception {
            throw new Exception();
        }

    }

    public void test() throws Exception {

        IocContainer container = new ZoomIocContainer();
        container.getIocClassLoader().appendModule(IocModule.class);
        container.getIocClassLoader().appendModule(AopModule.class);
        container.getIocClassLoader().appendModule(EventModule.class);

        container.fetch(IocModule.class);
        container.fetch(AopModule.class);
        container.fetch(EventModule.class);


        container.getIocClassLoader().appendModule(TestEventModule.class);


        TestEventModule module = container.fetch(TestEventModule.class);

        TestEventNotifier notifier = module.getNotifier();
        notifier.notifySuccess("success");

        try {
            notifier.notifySuccess1("success");

        } catch (Exception e) {

        }


        Thread.sleep(1000);

        assertEquals(counter.get(), 10);
    }
}
