package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.utils.StrKit;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.alias.AliasPolicy;
import org.zoomdev.zoom.dao.alias.AliasPolicyFactory;
import org.zoomdev.zoom.dao.alias.impl.CamelAliasPolicy;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.LinkedHashMap;
import java.util.Map;

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


    private static AliasPolicy getAliasPolicyForNames(AliasPolicyFactory maker, String[] names) {
        AliasPolicy aliasPolicy = maker.getAliasPolicy(names);
        if (aliasPolicy == null) {
            aliasPolicy = CamelAliasPolicy.DEFAULT;
        }
        return aliasPolicy;
    }


    public static interface ColumnRenameVisitor {
        /**
         * @param tableMeta
         * @param columnMeta
         * @param fieldName        实体类字段（虚拟）
         * @param selectColumnName 用于在select语句中的字段，可能会增加AS
         */
        void visit(TableMeta tableMeta, ColumnMeta columnMeta, String fieldName, String selectColumnName);
    }

    private static Map<String, ColumnRenameConfig> rename(Dao dao, TableMeta tableMeta) {


        AliasPolicyFactory maker = dao.getAliasPolicyMaker();
        AliasPolicy aliasPolicy = maker.getAliasPolicy(getColumnNames(tableMeta));
        Map<String,ColumnRenameConfig> config = new LinkedHashMap<String, ColumnRenameConfig>();
        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            String fieldName = aliasPolicy.getAlias(columnMeta.getName());

            config.put(fieldName,new ColumnRenameConfig(
                    tableMeta,
                    columnMeta,
                    columnMeta.getName(),
                    columnMeta.getName()
            ));
        }
        return config;

    }

    public static Map<String, ColumnRenameConfig> rename(Dao dao,
                                                         String table) {

        TableMeta tableMeta = dao.getDbStructFactory().getTableMeta(table);

        return rename(dao, tableMeta);

    }
    static class ColumnRenameConfig {
        TableMeta tableMeta;
        ColumnMeta columnMeta;
        String selectColumnName;

        // 可能为 table.column 或者 column
        String columnName;

        public ColumnRenameConfig(TableMeta tableMeta, ColumnMeta columnMeta, String selectColumnName,String columnName) {
            this.tableMeta = tableMeta;
            this.columnMeta = columnMeta;
            this.selectColumnName = selectColumnName;
            this.columnName = columnName;
        }

    }
    public static Map<String,ColumnRenameConfig>  rename(Dao dao, String[] tables) {
        AliasPolicyFactory maker = dao.getAliasPolicyMaker();
        AliasPolicy tableAliasPolicy = getAliasPolicyForNames(maker, tables);
        Map<String,ColumnRenameConfig> config = new LinkedHashMap<String, ColumnRenameConfig>();
        boolean first = true;
        for (String table : tables) {
            TableMeta tableMeta = dao.getDbStructFactory().getTableMeta(table);
            String tableAliasName = tableAliasPolicy.getAlias(table);
            // 取出每一个表的重命名策略
            AliasPolicy columnAliasPolicy = getAliasPolicyForNames(maker, getColumnNames(tableMeta));
            for (ColumnMeta columnMeta : tableMeta.getColumns()) {
                String columnAliasName = columnAliasPolicy.getAlias(columnMeta.getName());
                //如果是第一个表，则直接使用字段名称，否则使用table.column的形式
                String fieldName = first ? columnAliasName : (tableAliasName + StrKit.upperCaseFirst(columnAliasName));

                String selectColumnName = String.format("%s.%s AS %s", table,
                        dao.getDriver().protectColumn(columnMeta.getName()),
                        dao.getDriver().protectColumn(StrKit.toUnderLine(fieldName) + "_")
                );

                String columnName =  String.format("%s.%s", tableMeta.getName(), columnMeta.getName());

                config.put(fieldName,new ColumnRenameConfig(
                        tableMeta,
                        columnMeta,
                        selectColumnName,
                        columnName
                ));
            }
            if (first) first = false;
        }
        return config;
    }
}
