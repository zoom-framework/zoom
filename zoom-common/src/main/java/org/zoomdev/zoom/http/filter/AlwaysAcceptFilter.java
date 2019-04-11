package org.zoomdev.zoom.http.filter;

public class AlwaysAcceptFilter<T> implements Filter<T> {

    @Override
    public boolean accept(T value) {
        return true;
    }

}
