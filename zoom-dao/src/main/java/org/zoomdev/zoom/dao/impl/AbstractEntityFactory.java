package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.EntityFactory;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;

public abstract class AbstractEntityFactory implements EntityFactory {

    protected final Dao dao;


    protected AbstractEntityFactory(Dao dao) {
        this.dao = dao;
    }


    protected TableMeta getTableMeta(String tableName) {
        TableMeta meta = dao.getDbStructFactory().getTableMeta(tableName);
        return meta;
    }

    @Override
    public void clearCache() {

    }

}
