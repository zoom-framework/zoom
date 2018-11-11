package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.utils.StrKit;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DbStructManager;
import org.zoomdev.zoom.dao.alias.AliasPolicy;
import org.zoomdev.zoom.dao.alias.AliasPolicyMaker;
import org.zoomdev.zoom.dao.alias.impl.CamelAliasPolicy;
import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * 重命名相关策略
 */
class RenameUtils {
    static String[] getColumnNames(TableMeta meta) {

        String[] names = new String[meta.getColumns().length];
        int index = 0;
        for (ColumnMeta columnMeta : meta.getColumns()) {
            names[index++] = columnMeta.getName();
        }
        return names;
    }


    private static AliasPolicy getAliasPolicyForNames(AliasPolicyMaker maker,String[] names){
        AliasPolicy aliasPolicy = maker.getAliasPolicy(names);
        if (aliasPolicy == null) {
            aliasPolicy = CamelAliasPolicy.DEFAULT;
        }
        return aliasPolicy;
    }

    public static void rename(AliasPolicyMaker maker,
                              String[] tables,
                              Dao dao){
        AliasPolicy tableAliasPolicy = getAliasPolicyForNames(maker,tables);
        // 得到一个映射关系
        List<RecordEntityField> entityFields = new ArrayList<RecordEntityField>();

        boolean first = true;
        for (String table : tables) {
            TableMeta tableMeta = dao.getDbStructFactory().getTableMeta(table);
            String tableAliasName = tableAliasPolicy.getAlias(table);
            // 取出每一个表的重命名策略
            AliasPolicy columnAliasPolicy = getAliasPolicyForNames(maker,getColumnNames(tableMeta));
            for (ColumnMeta columnMeta : tableMeta.getColumns()) {
                String columnAliasName = columnAliasPolicy.getAlias(columnMeta.getName());
                //如果是第一个表，则直接使用字段名称，否则使用table.column的形式
                String fieldName = first ? columnAliasName : (tableAliasName + StrKit.upperCaseFirst(columnAliasName));
                if(first)first=false;
                String asColumnName = String.format("%s.%s AS %s_",table,
                        dao.getDriver().protectColumn(columnMeta.getName()),
                        dao.getDriver().protectColumn(StrKit.toUnderLine(fieldName) + "_")
                        );


            }

        }
    }
}
