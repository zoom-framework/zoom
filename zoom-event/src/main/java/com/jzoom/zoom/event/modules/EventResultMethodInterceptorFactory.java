package com.jzoom.zoom.event.modules;

import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.aop.MethodInvoker;
import com.jzoom.zoom.aop.factory.AnnotationMethodInterceptorFactory;
import com.jzoom.zoom.event.EventService;
import com.jzoom.zoom.event.annotations.EventResult;

import java.lang.reflect.Method;
import java.util.List;

class EventResultMethodInterceptorFactory extends AnnotationMethodInterceptorFactory<EventResult> {

    private EventService eventService;

    public EventResultMethodInterceptorFactory(EventService eventService){
        this.eventService = eventService;
    }
    @Override
    protected void createMethodInterceptors(EventResult annotation, Method method, List<MethodInterceptor> interceptors) {
        interceptors.add(new EventResultMethodIngerceptor(eventService,annotation.value()));
    }


    static class EventResultMethodIngerceptor implements MethodInterceptor{
        public EventResultMethodIngerceptor(EventService eventService, String name) {
            this.eventService = eventService;
            this.name = name;
        }

        private final EventService eventService;
        private final String name;
        @Override
        public void intercept(MethodInvoker invoker) throws Throwable {
            invoker.invoke();
            Object result = invoker.getReturnObject();
            eventService.notifyObservers(name,result);
        }
    }
}
