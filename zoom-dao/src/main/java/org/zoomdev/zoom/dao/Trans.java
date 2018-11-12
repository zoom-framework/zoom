package org.zoomdev.zoom.dao;

public interface Trans {

    void beginTransaction(int level) throws Throwable;

    void commit();

    void rollback();

}
