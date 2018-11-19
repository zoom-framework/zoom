package org.zoomdev.zoom.web.action;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Monitor {

    private AtomicInteger num = new AtomicInteger(0);

    private Object[] arguments;

    public void increase() {
        num.incrementAndGet();
    }

    public void setArguments(Object...arguments){
        this.arguments = arguments;
        num.incrementAndGet();
    }
    public Object[] arguments(){
        return arguments;
    }

    public int count() {
        return num.get();
    }
}
