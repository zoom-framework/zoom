package org.zoomdev.zoom.event;

import org.apache.commons.lang3.StringUtils;

public class PayloadEvent implements Event {

    public PayloadEvent(String name, Object payload,Throwable error) {
        this.name = name;
        this.payload = payload;
        this.error = error;
    }

    private String name;
    private Object payload;
    private Throwable error;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T> T getData() {
        return (T) payload;
    }

    @Override
    public boolean is(String _name) {
        return StringUtils.equals(_name, name);
    }

    @Override
    public Throwable getError() {
        return error;
    }
}
