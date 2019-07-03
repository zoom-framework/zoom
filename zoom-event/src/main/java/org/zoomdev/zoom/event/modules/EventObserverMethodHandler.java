package org.zoomdev.zoom.event.modules;

import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.utils.ArgGetter;
import org.zoomdev.zoom.common.utils.ValueGetter;
import org.zoomdev.zoom.common.utils.impl.DefArgGetter;
import org.zoomdev.zoom.common.utils.impl.EqValueGetter;
import org.zoomdev.zoom.event.Event;
import org.zoomdev.zoom.event.EventListener;
import org.zoomdev.zoom.event.EventService;
import org.zoomdev.zoom.event.annotations.EventObserver;
import org.zoomdev.zoom.ioc.IocMethodProxy;
import org.zoomdev.zoom.ioc.IocObject;
import org.zoomdev.zoom.ioc.impl.AnnotationMethodHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class EventObserverMethodHandler extends AnnotationMethodHandler<EventObserver> {

    private EventService eventService;

    private Map<String, EventListener> listenerMap = new ConcurrentHashMap<String, EventListener>();

    public EventObserverMethodHandler(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    protected void visit(IocObject target, EventObserver annotation, IocMethodProxy method) {
        String name = annotation.value();
        EventListener listener = new InnerMethodInvoker(target, method.getMethod());
        listenerMap.put(method.getUid(), listener);
        eventService.addListener(name, listener);
    }


    @Override
    protected void destroy(IocObject target, EventObserver annotation, IocMethodProxy method) {
        String name = annotation.value();
        String uid = method.getUid();
        EventListener listener = listenerMap.get(uid);
        eventService.removeListener(name, listener);
    }


    static ValueGetter<Event,Object> eventData = new ValueGetter<Event,Object>() {
        @Override
        public Object getValue(Event data) {
            return data.getData();
        }
    };

    static ValueGetter<Event,Object> eventError = new ValueGetter<Event,Object>() {
        @Override
        public Object getValue(Event data) {
            return data.getError();
        }
    };

    static ValueGetter<Event,Object> eventName = new ValueGetter<Event,Object>() {
        @Override
        public Object getValue(Event data) {
            return data.getName();
        }
    };

    static ValueGetter parseValueGetter(Class<?> type){
        if (Event.class.isAssignableFrom(type)) {
            return EqValueGetter.getter;
        }

        if(CharSequence.class.isAssignableFrom(type)){
            return eventName;
        }

        if(Throwable.class.isAssignableFrom(type)){
            return eventError;
        }

        return eventData;
    }


    static class InnerMethodInvoker implements EventListener {


        private ArgGetter argGetter;


        public InnerMethodInvoker(IocObject target, Method method) {
            this.target = target;
            this.method = method;
            Class<?>[] types = method.getParameterTypes();
            ValueGetter[] getters = new ValueGetter[types.length];
            for(int i=0; i < types.length; ++i){
                getters[i] = parseValueGetter(types[i]);
            }
            argGetter = new DefArgGetter(getters);
        }

        private IocObject target;
        private Method method;


        @Override
        public void onEvent(Event event) {
            try {
                method.invoke(target.get(),argGetter.getArgs(event));
            } catch (Exception e) {
                throw new ZoomException(e);
            }
        }
    }
}
