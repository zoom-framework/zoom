package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.annotations.Column;
import org.zoomdev.zoom.dao.annotations.ColumnIgnore;
import org.zoomdev.zoom.dao.migrations.DatabaseBuilder;

import java.lang.reflect.Field;

public class ZoomDatabaseBuilder implements DatabaseBuilder {

    private Dao dao;

    ZoomDatabaseBuilder(Dao dao) {
        this.dao = dao;
    }

    @Override
    public DatabaseBuilder dropIfExists(String table) {
        return null;
    }

    @Override
    public DatabaseBuilder createIfNotExists(String table) {
        return null;
    }

    @Override
    public DatabaseBuilder commments(String commments) {
        return null;
    }

    @Override
    public DatabaseBuilder createTable(String table) {
        return null;
    }

    @Override
    public DatabaseBuilder add(String column) {
        return null;
    }

    @Override
    public DatabaseBuilder modify(String table, String column) {
        return null;
    }

    @Override
    public DatabaseBuilder varchar(int len) {
        return null;
    }

    @Override
    public DatabaseBuilder text() {
        return null;
    }

    @Override
    public DatabaseBuilder timestamp() {
        return null;
    }

    @Override
    public DatabaseBuilder date() {
        return null;
    }

    @Override
    public DatabaseBuilder integer(int len) {
        return null;
    }

    @Override
    public DatabaseBuilder number() {
        return null;
    }

    @Override
    public DatabaseBuilder notNull() {
        return null;
    }

    @Override
    public DatabaseBuilder primaryKey() {
        return null;
    }

    @Override
    public DatabaseBuilder autoIncement() {
        return null;
    }

    @Override
    public DatabaseBuilder unique() {
        return null;
    }

    @Override
    public DatabaseBuilder index() {
        return null;
    }

    @Override
    public void build() {

    }

    @Override
    public void build(Class<?> type, boolean dropIfExists) {
        assert (type != null);
        Field[] fields = CachedClasses.getFields(type);
        if (fields.length == 0) {
            throw new DaoException("必须至少有一个字段");
        }

        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            if (field.isAnnotationPresent(ColumnIgnore.class)) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                continue;
            }


        }


    }
}
