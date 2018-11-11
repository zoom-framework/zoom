package org.zoomdev.zoom.common.utils;

public interface Converter<T, E> {
    E convert(T data);
}
