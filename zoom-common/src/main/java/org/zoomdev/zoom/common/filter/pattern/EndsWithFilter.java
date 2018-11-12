package org.zoomdev.zoom.common.filter.pattern;

import org.zoomdev.zoom.common.filter.Filter;

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
