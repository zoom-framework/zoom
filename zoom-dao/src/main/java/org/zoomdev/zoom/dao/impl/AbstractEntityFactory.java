package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.caster.ValueCaster;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;
import org.zoomdev.zoom.dao.validator.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEntityFactory {

    protected final Dao dao;


    interface ContextHandler<CONTEXT> {
        void handle(AbstractEntityField field, CONTEXT context);
    }


    interface ValueCasterCreator<CONTEXT> extends ContextCreator<CONTEXT, ValueCaster> {

    }

    interface StatementAdapterCreator<CONTEXT> extends ContextCreator<CONTEXT, StatementAdapter> {

    }

    interface ContextCreator<CONTEXT, T> {
        T create(CONTEXT context);
    }


    protected AbstractEntityFactory(Dao dao) {
        this.dao = dao;
    }


    protected TableMeta getTableMeta(String tableName) {
        TableMeta meta = dao.getDbStructFactory().getTableMeta(tableName);
        return meta;
    }


    private org.zoomdev.zoom.dao.validator.Validator createLengthValidator(ColumnMeta columnMeta) {
        if (columnMeta.getMaxLen() != 0) {

            //什么类型
            switch (columnMeta.getType()) {
                case Types.VARCHAR:
                case Types.CHAR:
                case Types.CLOB: {
                    //字节判断
                    return new ByteStringValidator(columnMeta.getMaxLen());
                }
                case Types.NVARCHAR:
                case Types.NCHAR:
                case Types.NCLOB: {
                    return new StringValidator(columnMeta.getMaxLen());
                }
                case Types.BLOB:
            }

        }

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

    protected List<Validator> createValidators(ColumnMeta columnMeta) {
        List<Validator> list = new ArrayList<Validator>();
        if (!columnMeta.isNullable()) {
            if (columnMeta.getDefaultValue() == null) {
                list.add(new NotNullValidator());
            }
        }

        Validator validator = createLengthValidator(columnMeta);
        if (validator != null) {
            list.add(validator);
        }


        return list;
    }

    public void clearCache() {

    }

}
