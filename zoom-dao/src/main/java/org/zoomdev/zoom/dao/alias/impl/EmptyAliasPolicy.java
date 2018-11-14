package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.dao.alias.AliasPolicy;

public class EmptyAliasPolicy implements AliasPolicy {

    public static final AliasPolicy DEFAULT = new EmptyAliasPolicy();

    @Override
    public String getAlias(String column) {
        return column;
    }

}
