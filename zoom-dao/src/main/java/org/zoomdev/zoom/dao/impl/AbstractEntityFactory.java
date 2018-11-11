package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.AutoField;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.EntityFactory;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEntityFactory implements EntityFactory {

    protected final Dao dao;


    protected AbstractEntityFactory(Dao dao) {
        this.dao = dao;
    }


    protected TableMeta getTableMeta(String tableName){
        TableMeta meta = dao.getDbStructFactory().getTableMeta(tableName);
        dao.getDbStructFactory().fill(meta);
        return meta;
    }

    protected static String[] getColumnNames(TableMeta meta) {

        String[] names = new String[meta.getColumns().length];
        int index = 0;
        for (ColumnMeta columnMeta : meta.getColumns()) {
            names[index++] = columnMeta.getName();
        }
        return names;
    }

}
