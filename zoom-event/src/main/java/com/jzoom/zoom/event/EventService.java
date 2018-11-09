package com.jzoom.zoom.event;


public interface EventService {
    void addListener(String name, EventListener listener);
    void removeListener(String name, EventListener listener);
    void notifyObservers(String name, Object data);
    void notifyObservers(Event event);
}
