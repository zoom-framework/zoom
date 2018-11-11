package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.utils.StrKit;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.alias.AliasPolicy;
import org.zoomdev.zoom.dao.alias.AliasPolicyMaker;
import org.zoomdev.zoom.dao.alias.impl.CamelAliasPolicy;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;

import javax.swing.table.TableColumn;
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




    public static interface ColumnRenameVisitor {
        /**
         *
         * @param tableMeta
         * @param columnMeta
         * @param fieldName                 实体类字段（虚拟）
         * @param selectColumnName          用于在select语句中的字段，可能会增加AS
         */
        void visit( TableMeta tableMeta,ColumnMeta columnMeta,String fieldName,String selectColumnName);
    }
    public static void rename(Dao dao,
                              TableMeta tableMeta,
                              ColumnRenameVisitor visitor){


        AliasPolicyMaker maker = dao.getAliasPolicyMaker();
        AliasPolicy aliasPolicy = maker.getAliasPolicy(getColumnNames(tableMeta));
        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            String field = aliasPolicy.getAlias(columnMeta.getName());
            visitor.visit(tableMeta,columnMeta,field,columnMeta.getName());
        }

    }
    public static void rename(Dao dao,
                              String table,
                              ColumnRenameVisitor visitor){

        TableMeta tableMeta = dao.getDbStructFactory().getTableMeta(table);
        dao.getDbStructFactory().fill(tableMeta);

        rename(dao,tableMeta,visitor);

    }
    public static void rename(Dao dao,
                              String[] tables,
                              ColumnRenameVisitor visitor){
        AliasPolicyMaker maker = dao.getAliasPolicyMaker();
        AliasPolicy tableAliasPolicy = getAliasPolicyForNames(maker,tables);

        boolean first = true;
        for (String table : tables) {
            TableMeta tableMeta = dao.getDbStructFactory().getTableMeta(table);
            dao.getDbStructFactory().fill(tableMeta);
            String tableAliasName = tableAliasPolicy.getAlias(table);
            // 取出每一个表的重命名策略
            AliasPolicy columnAliasPolicy = getAliasPolicyForNames(maker,getColumnNames(tableMeta));
            for (ColumnMeta columnMeta : tableMeta.getColumns()) {
                String columnAliasName = columnAliasPolicy.getAlias(columnMeta.getName());
                //如果是第一个表，则直接使用字段名称，否则使用table.column的形式
                String fieldName = first ? columnAliasName : (tableAliasName + StrKit.upperCaseFirst(columnAliasName));

                String selectColumnName = String.format("%s.%s AS %s",table,
                        dao.getDriver().protectColumn(columnMeta.getName()),
                        dao.getDriver().protectColumn(StrKit.toUnderLine(fieldName) + "_")
                        );
                visitor.visit(tableMeta,columnMeta,fieldName,selectColumnName);

            }
            if(first)first=false;
        }
    }
}
