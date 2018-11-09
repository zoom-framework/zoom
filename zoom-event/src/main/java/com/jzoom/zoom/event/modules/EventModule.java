package com.jzoom.zoom.event.modules;

import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.common.annotations.Inject;
import com.jzoom.zoom.common.annotations.IocBean;
import com.jzoom.zoom.common.annotations.Module;
import com.jzoom.zoom.event.EventService;
import com.jzoom.zoom.ioc.IocMethodVisitor;

@Module
public class EventModule {


    @IocBean
    public EventService getEventService(){
        return new EventServiceImpl();
    }

    @Inject
    public void init(AopFactory aopFactory, IocMethodVisitor methodVisitor, EventService eventService){
        methodVisitor.add(new EventObserverMethodHandler(eventService));
        aopFactory.methodInterceptorFactory(new EventResultMethodInterceptorFactory(eventService),0);
    }


}
