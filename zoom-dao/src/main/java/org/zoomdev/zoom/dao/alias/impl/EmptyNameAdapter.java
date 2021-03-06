package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.dao.alias.NameAdapter;

public class EmptyNameAdapter implements NameAdapter {

    public static final NameAdapter DEFAULT = new EmptyNameAdapter();


    @Override
    public String getFieldName(String column) {
        return column;
    }


    @Override
    public String getColumnName(String field) {
        return field;
    }

}
