package org.zoomdev.zoom.event.modules;

import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.MethodInvoker;
import org.zoomdev.zoom.aop.factory.AnnotationMethodInterceptorFactory;
import org.zoomdev.zoom.event.EventService;
import org.zoomdev.zoom.event.annotations.EventNotifier;

import java.lang.reflect.Method;
import java.util.List;

class EventResultMethodInterceptorFactory extends AnnotationMethodInterceptorFactory<EventNotifier> {

    private EventService eventService;

    public EventResultMethodInterceptorFactory(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    protected void createMethodInterceptors(EventNotifier annotation, Method method, List<MethodInterceptor> interceptors) {
        interceptors.add(new EventResultMethodIngerceptor(eventService, annotation.value()));
    }


    static class EventResultMethodIngerceptor implements MethodInterceptor {
        public EventResultMethodIngerceptor(EventService eventService, String name) {
            this.eventService = eventService;
            this.name = name;
        }

        private final EventService eventService;
        private final String name;

        @Override
        public void intercept(MethodInvoker invoker) throws Throwable {

            try{
                invoker.invoke();
                Object result = invoker.getReturnObject();
                eventService.notifyObservers(name, result,null);
            }catch (Throwable e){
                eventService.notifyObservers(name, null,e);
            }

        }
    }
}
