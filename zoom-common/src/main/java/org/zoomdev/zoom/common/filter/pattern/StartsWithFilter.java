package org.zoomdev.zoom.common.filter.pattern;

import org.zoomdev.zoom.common.filter.Filter;

public class StartsWithFilter implements Filter<String> {

    private String prefix;


    StartsWithFilter(String prefix) {
        this.prefix = prefix;
    }


    @Override
    public boolean accept(String value) {
        if (value == null) return false;
        return value.startsWith(prefix);
    }

}
