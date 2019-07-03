package org.zoomdev.zoom.common.utils.impl;

import org.zoomdev.zoom.common.utils.ValueGetter;

public class EqValueGetter<T, V> implements ValueGetter<T, T> {


    public static final EqValueGetter getter = new EqValueGetter();

    @Override
    public T getValue(T data) {
        return data;
    }
}
