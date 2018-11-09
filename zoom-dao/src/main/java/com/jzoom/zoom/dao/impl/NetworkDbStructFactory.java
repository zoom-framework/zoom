package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.driver.DbStructFactory;
import com.jzoom.zoom.dao.meta.TableMeta;

import java.util.Collection;

public class NetworkDbStructFactory implements DbStructFactory {

    NetworkAdapter networkAdapter;


    /**
     * 这些都有对应的http接口
     * @param ar
     * @return
     */
    @Override
    public Collection<String> getTableNames(Ar ar) {
        return null;
    }

    @Override
    public TableMeta getTableMeta(Ar ar, String tableName) {
        return null;
    }

    @Override
    public void fill(Ar ar, TableMeta meta) {

    }

    @Override
    public Collection<TableNameAndComment> getNameAndComments(Ar ar) {
        return null;
    }

    @Override
    public void clearCache() {

    }
}
