package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.dao.alias.AliasPolicy;

public class ToLowerCaseAiias implements AliasPolicy {

    @Override
    public String getAlias(String column) {
        return column.toLowerCase();
    }

}
