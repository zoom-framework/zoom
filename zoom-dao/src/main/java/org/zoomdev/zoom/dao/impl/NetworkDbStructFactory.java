package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.Collection;

public class NetworkDbStructFactory implements DbStructFactory {

    NetworkAdapter networkAdapter;

    @Override
    public Collection<String> getTableNames() {
        return null;
    }

    @Override
    public TableMeta getTableMeta(String tableName) {
        return null;
    }

    @Override
    public void fill(TableMeta meta) {

    }

    @Override
    public Collection<TableNameAndComment> getNameAndComments() {
        return null;
    }

    @Override
    public void clearCache() {

    }
}
