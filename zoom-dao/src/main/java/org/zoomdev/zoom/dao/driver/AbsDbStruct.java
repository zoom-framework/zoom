package org.zoomdev.zoom.dao.driver;

import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.dao.ConnectionExecutor;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;
import org.zoomdev.zoom.dao.utils.DaoUtils;
import org.zoomdev.zoom.dao.validator.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbsDbStruct implements DbStructFactory {

    public static final Validator[] EMPTY = new Validator[0];

    protected final Dao dao;

    public AbsDbStruct(Dao dao) {
        this.dao = dao;
    }


    protected String getQueryTableMetaStatement(String table) {
        return "select * from " + dao.getDriver().protectTable(table) + " where 1=2";
    }

    protected Map<String,List<ColumnMeta>> toTreeMap(List<ColumnMeta> columnMetas){

        Map<String,List<ColumnMeta>> map = new HashMap<String, List<ColumnMeta>>();
        for(ColumnMeta columnMeta : columnMetas){
            List<ColumnMeta> list = map.get(columnMeta.getTable());
            if(list==null){
                list = new ArrayList<ColumnMeta>();
                map.put(columnMeta.getTable().toLowerCase(),list);
            }
            list.add(columnMeta);
        }

        return map;

    }

    @Override
    public TableMeta getTableMeta(final String table) {
        TableMeta tableMeta = dao.ar().execute(new ConnectionExecutor() {

            @SuppressWarnings("unchecked")
            @Override
            public TableMeta execute(Connection connection) throws SQLException {
                PreparedStatement statement = null;
                ResultSet rs = null;
                try {
                    statement = connection.prepareStatement(getQueryTableMetaStatement(table));
                    rs = statement.executeQuery();
                    ResultSetMetaData data = rs.getMetaData();
                    List<ColumnMeta> columnMetas = new ArrayList<ColumnMeta>(data.getColumnCount());
                    for (int i = 1, c = data.getColumnCount(); i <= c; ++i) {
                        ColumnMeta column = new ColumnMeta();
                        String className = data.getColumnClassName(i);
                        column.setDataType(Classes.forName(className));
                        column.setTable(table);
                        column.setName(data.getColumnName(i));
                        column.setType(data.getColumnType(i));
                        column.setRawType(data.getColumnTypeName(i));

                        columnMetas.add(column);
                    }

                    TableMeta meta = new TableMeta();
                    meta.setName(table);
                    meta.setColumns(columnMetas.toArray(new ColumnMeta[columnMetas.size()]));
                    return meta;
                } finally {
                    DaoUtils.close(statement);
                    DaoUtils.close(rs);
                }

            }

        });

        if (tableMeta.getComment() == null) {
            fill(tableMeta);
            for(ColumnMeta column : tableMeta.getColumns()){
                column.setValidators(createValidators(column));
            }
            if (tableMeta.getComment() == null) {
                tableMeta.setComment("");
            }
        }




        return tableMeta;
    }

    public abstract void fill(TableMeta meta);

    /**
     * 清除缓存
     */
    public void clearCache() {

    }


    private Validator createLengthValidator(ColumnMeta columnMeta) {
//        if (columnMeta.getMaxLen() != 0) {
//
//            //什么类型
//            switch (columnMeta.getType()) {
//                case Types.VARCHAR:
//                case Types.CHAR:
//                case Types.CLOB: {
//                    //字节判断
//                    return new ByteStringValidator(columnMeta.getMaxLen());
//                }
//                case Types.NVARCHAR:
//                case Types.NCHAR:
//                case Types.NCLOB: {
//                    return new StringValidator(columnMeta.getMaxLen());
//                }
//                case Types.BLOB:
//            }
//
//        }

        return null;
    }


    private Validator createFormatValidator(ColumnMeta columnMeta) {
        //什么类型
        switch (columnMeta.getType()) {
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.SMALLINT: {
                return IntegerValidator.DEFAULT;
            }
            case Types.NUMERIC:
            case Types.DECIMAL: {
                return NumberValidator.DEFAULT;
            }
            case Types.BOOLEAN: {

            }
        }
        return null;
    }

    protected Validator[] createValidators(ColumnMeta columnMeta) {
        List<Validator> list = new ArrayList<Validator>();

        if(!columnMeta.isAuto()){
            if (!columnMeta.isNullable() ) {
                if (columnMeta.getDefaultValue() == null) {
                    list.add(new NotNullValidator());
                }
            }
        }


        Validator validator = createLengthValidator(columnMeta);
        if (validator != null) {
            list.add(validator);
        }

        validator = createFormatValidator(columnMeta);
        if(validator!=null){
            list.add(validator);
        }

        if(list.size()==0){
            return EMPTY;
        }

        return list.toArray(new Validator[list.size()]);
    }


    @Override
    public abstract Snapshot takeSnapshot();
}
