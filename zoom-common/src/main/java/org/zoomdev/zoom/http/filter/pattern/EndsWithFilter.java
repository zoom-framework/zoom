package org.zoomdev.zoom.http.filter.pattern;

import org.zoomdev.zoom.http.filter.Filter;

public class EndsWithFilter implements Filter<String> {

    private String prefix;


    EndsWithFilter(String prefix) {
        this.prefix = prefix;
    }


    @Override
    public boolean accept(String value) {
        if (value == null) return false;
        return value.endsWith(prefix);
    }

}
