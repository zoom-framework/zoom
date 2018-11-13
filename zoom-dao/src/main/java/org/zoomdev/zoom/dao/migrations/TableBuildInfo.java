package org.zoomdev.zoom.dao.migrations;

import org.zoomdev.zoom.dao.meta.ColumnMeta;

import java.util.ArrayList;
import java.util.List;

public class TableBuildInfo {
    boolean createWhenNotExists = false;
    String comment;
    String name;
    List<ColumnMeta> columns = new ArrayList<ColumnMeta>();

    public boolean isCreateWhenNotExists() {
        return createWhenNotExists;
    }


    public String getComment() {
        return comment;
    }


    public String getName() {
        return name;
    }


    public List<ColumnMeta> getColumns() {
        return columns;
    }


}