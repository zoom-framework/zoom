package org.zoomdev.zoom.http.filter;

/**
 * 所有过滤器中，只要有一个通过，就算通过
 *
 * @param <T>
 * @author jzoom
 */
public class OrFilter<T> implements Filter<T> {

    private Filter<T>[] filters;

    public OrFilter(Filter<T>... filters) {
        this.filters = filters;
    }

    @Override
    public boolean accept(T value) {
        for (Filter<T> filter : filters) {
            if (filter.accept(value)) {
                return true;
            }
        }
        return false;
    }

}
