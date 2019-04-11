package org.zoomdev.zoom.http.filter.impl;

import org.zoomdev.zoom.http.Destroyable;
import org.zoomdev.zoom.http.filter.Filter;
import org.zoomdev.zoom.http.filter.pattern.PatternFilterFactory;

public class ClassNameFilter implements Filter<Class<?>>, Destroyable {

    private Filter<String> filter;

    public ClassNameFilter(String pattern) {
        this.filter = PatternFilterFactory.createFilter(pattern);
    }

    @Override
    public boolean accept(Class<?> value) {
        return filter.accept(value.getName());
    }

    @Override
    public void destroy() {
        this.filter = null;
    }

}
