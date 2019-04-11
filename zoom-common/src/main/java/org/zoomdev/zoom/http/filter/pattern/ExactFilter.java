package org.zoomdev.zoom.http.filter.pattern;

import org.zoomdev.zoom.http.filter.Filter;

/**
 * 精确匹配
 *
 * @author jzoom
 */
public class ExactFilter implements Filter<String> {
    private String prefix;


    ExactFilter(String prefix) {
        this.prefix = prefix;
    }


    @Override
    public boolean accept(String value) {
        if (value == null) return false;
        return value.equals(prefix);
    }

}
