package org.zoomdev.zoom.dao.driver.impl;

import org.zoomdev.zoom.dao.driver.Snapshot;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.io.Serializable;
import java.util.List;

public class ZoomSnapshot implements Snapshot,Serializable {


    public List<TableMeta> getTables() {
        return tables;
    }

    public void setTables(List<TableMeta> tables) {
        this.tables = tables;
    }

    private List<TableMeta> tables;



    @Override
    public String compare(Snapshot snapshot) {
        return null;
    }

    @Override
    public String toSql(SqlDriver driver) {



        return null;
    }
}
