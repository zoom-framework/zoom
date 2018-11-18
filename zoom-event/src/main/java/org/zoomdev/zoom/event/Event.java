package org.zoomdev.zoom.event;

public interface Event {

    String getName();

    <T> T getData();

    boolean is(String name);

    Throwable getError();

}
