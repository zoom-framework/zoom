package com.jzoom.zoom.event;

public interface Event {

    String getName();

    <T> T getData();

    boolean is(String name);

}
