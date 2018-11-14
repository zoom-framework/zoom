package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.dao.alias.NameAdapter;

public class ToLowerCaseNameAdapter implements NameAdapter {

    public static final NameAdapter DEFAULT = new ToLowerCaseNameAdapter();

    @Override
    public String getSelectColumn(String field) {
        return field.toUpperCase();
    }

    @Override
    public String getFieldName(String column) {
        return column.toLowerCase();
    }

    @Override
    public String getOrgFieldName(String column) {
        return column.toLowerCase();
    }

    @Override
    public String getColumnName(String field) {
        return field.toUpperCase();
    }

    @Override
    public String getSelectField(String column) {
        return getFieldName(column);
    }

}
