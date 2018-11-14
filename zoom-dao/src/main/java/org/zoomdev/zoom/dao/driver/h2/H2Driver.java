package org.zoomdev.zoom.dao.driver.h2;

import org.zoomdev.zoom.dao.driver.mysql.MysqlDriver;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.migrations.TableBuildInfo;
import org.zoomdev.zoom.dao.migrations.ZoomDatabaseBuilder;

import java.util.ArrayList;
import java.util.List;

public class H2Driver extends MysqlDriver {

    @Override
    public void build(TableBuildInfo table, StringBuilder sb) {


        List<String> primaryKeys = new ArrayList<String>(3);

        for (ColumnMeta columnMeta : table.getColumns()) {
            if(columnMeta.isPrimary()){
                primaryKeys.add(columnMeta.getName());
            }
        }

        sb.append("CREATE TABLE ");
        if (table.isCreateWhenNotExists()) {
            sb.append("IF NOT EXISTS ");
        }
        protectTable(sb, table.getName());
        sb.append("(\n");
        boolean first = false;
        int index = 0;
        for (ColumnMeta columnMeta : table.getColumns()) {
            sb.append("\t");
            protectColumn(sb, columnMeta.getName());
            sb.append(' ');
            try{
                sb.append(formatColumnType(columnMeta));
            }catch (Exception e){
                throw new RuntimeException("不支持的类型"+columnMeta.getName());
            }



            if (columnMeta.getDefaultValue() != null) {
                if (columnMeta.getDefaultValue() instanceof String) {
                    sb.append(" DEFAULT '").append(columnMeta.getDefaultValue()).append("'");
                } else {
                    if(columnMeta.getDefaultValue() instanceof ZoomDatabaseBuilder.FunctionValue){
                        sb.append(" DEFAULT ").append(((ZoomDatabaseBuilder.FunctionValue)columnMeta.getDefaultValue()).getValue());
                    }else{
                        sb.append(" DEFAULT ").append(columnMeta.getDefaultValue());
                    }

                }
            }else{
                if(columnMeta.isPrimary()){
                    if(columnMeta.isAuto()){
                        sb.append(" PRIMARY KEY");
                        sb.append(" auto_increment".toUpperCase());
                    }else{
                        //single primary key
                        if(primaryKeys.size() == 1){
                            sb.append(" PRIMARY KEY");
                        }
                    }
                }else{
                    sb.append(columnMeta.isNullable()
                            ? " NULL"
                            : " NOT NULL");
                }
            }
            //sb.append(" COMMENT '").append(columnMeta.getComment()==null ? "":columnMeta.getComment()).append("'");

            if( index < table.getColumns().size() - 1){
                sb.append(",");
            }

            if(index == table.getColumns().size()-1){
                break;
            }
            sb.append("\n");
            ++index;
        }

        if(primaryKeys.size() > 1){
            first = true;

            sb.append(",\n\tPRIMARY KEY (");
            for(String key : primaryKeys){
                if(first){
                    first = false;
                }else{
                    sb.append(",");
                }
                sb.append(key);
            }
            sb.append(")\n");

        }else{
            sb.append("\n");
        }

        sb.append(")charset=utf8;\n");

        //index

        for (ColumnMeta columnMeta : table.getColumns()) {
            if(columnMeta.isIndex()){
                sb.append("CREATE INDEX ")
                        .append("IDX_")
                        .append(table.getName())
                        .append("_")
                        .append(columnMeta.getName())
                        .append(" ON ")
                        .append(table.getName())
                        .append("(")
                        .append(columnMeta.getName())
                        .append(");\n");
            }
        }

        sb.append("\n");

    }



}
