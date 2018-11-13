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

    public void setCreateWhenNotExists(boolean createWhenNotExists) {
        this.createWhenNotExists = createWhenNotExists;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ColumnMeta> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnMeta> columns) {
        this.columns = columns;
    }
}