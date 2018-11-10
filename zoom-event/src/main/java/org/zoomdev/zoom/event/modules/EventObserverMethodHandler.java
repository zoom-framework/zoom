package org.zoomdev.zoom.event.modules;

import org.zoomdev.zoom.event.Event;
import org.zoomdev.zoom.event.EventListener;
import org.zoomdev.zoom.event.EventService;
import org.zoomdev.zoom.event.annotations.EventObserver;
import org.zoomdev.zoom.ioc.IocMethod;
import org.zoomdev.zoom.ioc.IocObject;
import org.zoomdev.zoom.ioc.impl.AnnotationMethodHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class EventObserverMethodHandler extends AnnotationMethodHandler<EventObserver> {

    private EventService eventService;

    private Map<String,EventListener> listenerMap = new ConcurrentHashMap<String, EventListener>();

    public EventObserverMethodHandler(EventService eventService){
        this.eventService = eventService;
    }

    @Override
    protected void visit(IocObject target, EventObserver annotation, IocMethod method) {
        String name = annotation.value();
        EventListener listener = new InnerMethodInvoker(target,method.getMethod());
        listenerMap.put(method.getUid(),listener);
        eventService.addListener(name,listener);
    }


    @Override
    protected void destroy(IocObject target, EventObserver annotation, IocMethod method) {
        String name = annotation.value();
        String uid = method.getUid();
        EventListener listener = listenerMap.get(uid);
        eventService.removeListener(name,listener);
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
