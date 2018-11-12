package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.common.utils.StrKit;
import org.zoomdev.zoom.dao.adapters.NameAdapter;

/**
 * 驼峰式命名规则
 *
 * @author jzoom
 */
public class CamelNameAdapter implements NameAdapter {

    public static final NameAdapter ADAPTER = new CamelNameAdapter();


    public CamelNameAdapter() {
    }

    @Override
    public String getSelectColumn(String field) {
        return getColumnName(field);
    }

    @Override
    public String getFieldName(String column) {
        return CamelAliasPolicy.DEFAULT.getAlias(column);
    }

    @Override
    public String getOrgFieldName(String column) {
        return CamelAliasPolicy.DEFAULT.getAlias(column);
    }

    @Override
    public String getColumnName(String field) {

        return StrKit.toUnderLine(field);
    }

    @Override
    public String getSelectField(String column) {
        return getFieldName(column);
    }


}
