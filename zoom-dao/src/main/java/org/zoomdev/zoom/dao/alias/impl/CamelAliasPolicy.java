package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.http.utils.StrKit;
import org.zoomdev.zoom.dao.alias.AliasPolicy;

public class CamelAliasPolicy implements AliasPolicy {
    public static final AliasPolicy DEFAULT = new CamelAliasPolicy();

    @Override
    public String getAlias(String column) {

        return StrKit.toCamel(column);
    }

}
