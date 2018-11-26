package org.zoomdev.zoom.dao.alias.impl;


/**
 * 使用前缀来修改名称的策略
 *
 * PrefixAliasPolicy policy = new PrefixAliasPolicy("NAME_");
 * assertTrue(policy.getAlias("NAME_ID").equals("id"));
 *
 */
public class PrefixAliasPolicy extends CamelAliasPolicy {

    private String prefix;

    public PrefixAliasPolicy(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getAlias(String column) {
        if (column.startsWith(prefix)) {
            column = column.substring(prefix.length());
        }

        return super.getAlias(column);

    }


}
