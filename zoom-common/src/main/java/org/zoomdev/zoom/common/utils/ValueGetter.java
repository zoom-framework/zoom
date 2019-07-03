package org.zoomdev.zoom.common.utils;

public interface ValueGetter<T, V> {
    V getValue(T data);
}
