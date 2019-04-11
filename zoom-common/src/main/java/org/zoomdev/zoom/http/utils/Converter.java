package org.zoomdev.zoom.http.utils;

public interface Converter<T, E> {
    E convert(T data);
}
