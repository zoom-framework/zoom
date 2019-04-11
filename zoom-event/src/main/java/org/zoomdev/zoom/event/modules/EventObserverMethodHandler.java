package org.zoomdev.zoom.event.modules;

import org.zoomdev.zoom.http.exceptions.ZoomException;
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

    static class InnerMethodInvoker implements EventListener {

        private int condition;

        public InnerMethodInvoker(IocObject target, Method method) {
            this.target = target;
            this.method = method;
            Class<?>[] types = method.getParameterTypes();
            if (types.length == 0) {
                condition = 0;
            } else if (types.length == 1) {
                Class<?> type = types[0];
                if (Event.class.isAssignableFrom(type)) {
                    condition = 1;
                } else if (CharSequence.class.isAssignableFrom(type)) {
                    condition = 2;
                } else {
                    throw new ZoomException("EventObserver标注的方法如果参数个数为1个，必须是Event或者String类型");
                }
            } else if (types.length == 2) {

                if (CharSequence.class.isAssignableFrom(types[0])) {
                    condition = 3;

                } else {
                    throw new ZoomException("EventObserver标注的方法如果参数个数为2个，类型必须分别为String和EventNotifier标注方法的返回类型的超类");
                }


            } else if (types.length == 3) {

                if (CharSequence.class.isAssignableFrom(types[0])

                        && Throwable.class.isAssignableFrom(types[2])) {
                    condition = 4;

                } else {
                    throw new ZoomException("EventObserver标注的方法如果参数个数为3个，类型必须分别为String、EventNotifier标注方法的返回类型的超类和Throwable的子类");
                }

            } else {
                throw new ZoomException("EventObserver标注的方法如果参数个数最多支持三个");
            }
        }

        private IocObject target;
        private Method method;


        @Override
        public void onEvent(Event event) {

            try {
                final int condition = this.condition;
                if (condition == 0) {
                    //没有参数
                    method.invoke(target.get());
                } else if (condition == 1) {
                    // 参数一个Event
                    method.invoke(target.get(), event);
                } else if (condition == 2) {
                    // 参数是name
                    method.invoke(target.get(), event.getName());
                } else if (condition == 3) {
                    //参数是name + object
                    method.invoke(target.get(), event.getName(), event.getData());
                } else if (condition == 4) {
                    //参数是name+data+throwable
                    method.invoke(target.get(), event.getName(), event.getData(), event.getError());
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
