package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.dao.auto.AutoField;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;

public interface AutoGenerateFactory {


    AutoField create(Dao dao, TableMeta table, ColumnMeta columnMeta);

}
