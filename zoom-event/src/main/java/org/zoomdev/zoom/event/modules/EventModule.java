package org.zoomdev.zoom.event.modules;

import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.event.EventService;
import org.zoomdev.zoom.ioc.IocMethodVisitor;

@Module
public class EventModule {


    @IocBean
    public EventService getEventService() {
        return new EventServiceImpl();
    }

    @Inject
    public void init(AopFactory aopFactory, IocMethodVisitor methodVisitor, EventService eventService) {
        methodVisitor.add(new EventObserverMethodHandler(eventService));
        aopFactory.addMethodInterceptorFactory(new EventResultMethodInterceptorFactory(eventService), 0);
    }


}
