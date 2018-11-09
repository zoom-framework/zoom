package com.jzoom.zoom.event.modules;

import com.jzoom.zoom.event.Event;
import com.jzoom.zoom.event.EventListener;
import com.jzoom.zoom.event.EventService;
import com.jzoom.zoom.event.annotations.EventObserver;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocMethodProxy;
import com.jzoom.zoom.ioc.IocObject;
import com.jzoom.zoom.ioc.impl.AnnotationMethodHandler;

import java.lang.reflect.Method;

class EventObserverMethodHandler extends AnnotationMethodHandler<EventObserver> {

    private EventService eventService;
    private IocContainer ioc;

    public EventObserverMethodHandler(EventService eventService){
        this.eventService = eventService;
    }

    @Override
    protected void visit(IocObject target, EventObserver annotation, Method method, IocMethodProxy proxy) {
        String name = annotation.value();
        eventService.addListener(name,new InnerMethodInvoker(target,method));
    }

    static class InnerMethodInvoker implements EventListener{

        private int condition;

        public InnerMethodInvoker(IocObject target, Method method) {
            this.target = target;
            this.method = method;
            Class<?>[] types = method.getParameterTypes();
            if(types.length == 0){
                condition = 0;
            }else if(types.length == 1){
                Class<?> type = types[0];
                if(Event.class.isAssignableFrom(type)){
                    condition = 1;
                }else{
                    condition = 2;
                }
            }
        }

        private IocObject target;
        private Method method;


        @Override
        public void onEvent(Event event) {

            try {
                final int condition = this.condition;
                if(condition == 0){
                    method.invoke(target.get());
                }else if(condition == 1){
                    method.invoke(target.get(), event);
                }else{
                    method.invoke(target.get(), event.getName());
                }

            } catch (Exception e) {
               throw new RuntimeException(e);
            }
        }
    }
}
