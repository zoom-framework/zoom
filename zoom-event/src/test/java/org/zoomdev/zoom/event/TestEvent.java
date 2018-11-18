package org.zoomdev.zoom.event;

import junit.framework.TestCase;
import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.aop.modules.AopModule;
import org.zoomdev.zoom.event.annotations.EventObserver;
import org.zoomdev.zoom.event.modules.EventModule;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.impl.ZoomIocContainer;
import org.zoomdev.zoom.ioc.impl.ZoomIocKey;
import org.zoomdev.zoom.ioc.modules.IocModule;

public class TestEvent extends TestCase {


    public static class TestEventObserver{

        @EventObserver("onSuccess")
        public void onEvent(Object data){


        }


    }
    public static class TestEventB{



    }
    public void test(){

        IocContainer container = new ZoomIocContainer();
        container.getIocClassLoader().appendModule(IocModule.class);
        container.getIocClassLoader().appendModule(AopModule.class);
        container.getIocClassLoader().appendModule(EventModule.class);

        AopFactory factory = (AopFactory) container.fetch(new ZoomIocKey(AopFactory.class)).get();






    }
}
