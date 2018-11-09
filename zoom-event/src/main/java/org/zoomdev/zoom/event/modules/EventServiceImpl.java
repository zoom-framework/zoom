package org.zoomdev.zoom.event.modules;

import org.zoomdev.zoom.common.async.Asyncs;
import org.zoomdev.zoom.common.designpattern.SingletonUtils;
import org.zoomdev.zoom.common.lock.LockUtils;
import org.zoomdev.zoom.event.Event;
import org.zoomdev.zoom.event.EventListener;
import org.zoomdev.zoom.event.EventService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 *
 * lock针对events的List对象进行保护
 *
 */
class EventServiceImpl implements EventService {

    private Map<String,List<EventListener>> events = new ConcurrentHashMap<String, List<EventListener>>();

    public void addListener(String name, final EventListener listener){
        SingletonUtils.modify(
                events,
                name,
                new SingletonUtils.SingletonModify<List<EventListener>>() {
                    @Override
                    public List<EventListener> modify(List<EventListener> eventListeners) {
                        eventListeners.add(listener);
                        return eventListeners;
                    }

                    @Override
                    public List<EventListener> create() {
                        return Arrays.asList(listener);
                    }
                });
    }

    public void removeListener(String name, final EventListener listener){
        List<EventListener> listeners = events.get(name);
        synchronized (LockUtils.getLock(name)){
            listeners.remove(listener);
        }
    }

    public void notifyObservers(final String name, final Object data){
        notifyObservers(new Event() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public <T> T getData() {
                return (T) data;
            }

            @Override
            public boolean is(String _name) {
                return StringUtils.equals(name,_name);
            }
        });
    }

    public void notifyObservers(final Event event){
        List<EventListener> listeners = events.get(event.getName());

        List<EventListener> eventListeners = new ArrayList<EventListener>();
        synchronized (LockUtils.getLock(event.getName())){
            eventListeners.addAll(listeners);
        }

        for(final EventListener listener : eventListeners){
            Asyncs.defaultJobQueue().run(new Runnable() {
                @Override
                public void run() {
                    listener.onEvent(event);
                }
            });
        }

    }

}
