package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.EAr;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.adapters.StatementAdapter;
import com.jzoom.zoom.dao.alias.NameAdapterFactory;
import com.jzoom.zoom.dao.driver.DbStructFactory;
import com.jzoom.zoom.dao.driver.SqlDriver;

import javax.sql.DataSource;

public class HttpDao implements Dao {



    @Override
    public Ar ar() {
        return null;
    }

    @Override
    public Ar getAr() {
        return null;
    }

    @Override
    public Ar table(String table) {
        return null;
    }

    @Override
    public Ar tables(String[] tables) {
        return null;
    }

    @Override
    public <T> EAr<T> ar(Class<T> type) {
        return null;
    }

    @Override
    public EAr<Record> record(String table) {
        return null;
    }

    @Override
    public DbStructFactory getDbStructFactory() {
        return null;
    }

    @Override
    public NameAdapterFactory getNameAdapterFactory() {
        return null;
    }

    @Override
    public void clearCache() {

    }

    @Override
    public SqlDriver getDriver() {
        return null;
    }

    @Override
    public DataSource getDataSource() {
        return null;
    }

    @Override
    public StatementAdapter getStatementAdapter(Class<?> fieldType, Class<?> columnType) {
        return null;
    }

    @Override
    public StatementAdapter getStatementAdapter(Class<?> columnType) {
        return null;
    }
}
