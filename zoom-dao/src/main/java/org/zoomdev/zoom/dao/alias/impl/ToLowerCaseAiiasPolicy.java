package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.dao.alias.AliasPolicy;

public class ToLowerCaseAiiasPolicy implements AliasPolicy {

    public static final AliasPolicy DEFAULT = new ToLowerCaseAiiasPolicy();

    private ToLowerCaseAiiasPolicy(){

    }

    @Override
    public String getAlias(String column) {
        return column.toLowerCase();
    }

}
