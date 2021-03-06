package org.zoomdev.zoom.event.modules;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.async.impl.Asyncs;
import org.zoomdev.zoom.common.designpattern.SingletonUtils;
import org.zoomdev.zoom.common.lock.LockUtils;
import org.zoomdev.zoom.event.Event;
import org.zoomdev.zoom.event.EventListener;
import org.zoomdev.zoom.event.EventService;
import org.zoomdev.zoom.event.PayloadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * lock针对events的List对象进行保护
 */
class AsyncEventService implements EventService {

    private Map<String, List<EventListener>> events = new ConcurrentHashMap<String, List<EventListener>>();

    /**
     * @param name
     * @param listener
     */
    public void addListener(final String name, final EventListener listener) {
        SingletonUtils.modify(
                events,
                name,
                new SingletonUtils.SingletonModify<List<EventListener>>() {
                    @Override
                    public List<EventListener> modify(List<EventListener> eventListeners) {
                        synchronized (LockUtils.getLock(name)) {
                            eventListeners.add(listener);
                        }
                        return eventListeners;
                    }

                    @Override
                    public List<EventListener> create() {
                        List<EventListener> listeners = new ArrayList<EventListener>();
                        listeners.add(listener);
                        return listeners;
                    }
                }
        );
    }

    /**
     * @param name
     * @param listener
     */
    public void removeListener(String name, final EventListener listener) {
        List<EventListener> listeners = events.get(name);
        synchronized (LockUtils.getLock(name)) {
            listeners.remove(listener);
        }
    }

    /**
     * @param name
     * @param data
     * @param error
     */
    public void notifyObservers(final String name, final Object data, final Throwable error) {
        notifyObservers(new PayloadEvent(name, data, error));
    }

    private static final Log log = LogFactory.getLog(AsyncEventService.class);

    /**
     * @param event
     */
    public void notifyObservers(final Event event) {
        List<EventListener> listeners = events.get(event.getName());
        if (listeners == null) {
            log.info("没有监听器来处理事件" + event.getName());
            return;
        }
        List<EventListener> eventListeners = new ArrayList<EventListener>();
        synchronized (LockUtils.getLock(event.getName())) {
            eventListeners.addAll(listeners);
        }

        for (final EventListener listener : eventListeners) {
            Asyncs.defaultJobQueue().run(new Runnable() {
                @Override
                public void run() {
                    listener.onEvent(event);
                }
            });
        }

    }

}
