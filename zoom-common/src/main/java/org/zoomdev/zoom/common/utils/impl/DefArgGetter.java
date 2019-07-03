package org.zoomdev.zoom.common.utils.impl;

import org.zoomdev.zoom.common.utils.ArgGetter;
import org.zoomdev.zoom.common.utils.ValueGetter;

public class DefArgGetter<T> implements ArgGetter<T> {

    private final ValueGetter<T, Object>[] getters;

    public DefArgGetter(ValueGetter<T, Object>... valueGetters) {
        this.getters = valueGetters;
    }


    @Override
    public Object[] getArgs(T data) {
        int c = getters.length;
        Object[] objects = new Object[c];
        for (int i = 0; i < c; ++i) {
            objects[i] = getters[i].getValue(data);
        }
        return objects;
    }
}
