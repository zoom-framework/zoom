package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.caster.ValueCaster;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;
import org.zoomdev.zoom.dao.validator.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEntityFactory {

    protected final Dao dao;


    interface ContextHandler<CONTEXT> {
        void handle(AbstractEntityField field, CONTEXT context);
    }


    interface ValueCasterCreator<CONTEXT> extends ContextCreator<CONTEXT, ValueCaster> {

    }

    interface StatementAdapterCreator<CONTEXT> extends ContextCreator<CONTEXT, StatementAdapter> {

    }

    interface ContextCreator<CONTEXT, T> {
        T create(CONTEXT context);
    }


    protected AbstractEntityFactory(Dao dao) {
        this.dao = dao;
    }


    protected TableMeta getTableMeta(String tableName) {
        TableMeta meta = dao.getDbStructFactory().getTableMeta(tableName);
        return meta;
    }


    public void clearCache() {

    }

}
