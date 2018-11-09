package org.zoomdev.zoom.event.modules;

import org.zoomdev.zoom.event.Event;
import org.zoomdev.zoom.event.EventListener;
import org.zoomdev.zoom.event.EventService;
import org.zoomdev.zoom.event.annotations.EventObserver;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.IocMethodProxy;
import org.zoomdev.zoom.ioc.IocObject;
import org.zoomdev.zoom.ioc.impl.AnnotationMethodHandler;

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
