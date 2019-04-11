package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.http.utils.DataObject;

import java.lang.reflect.Type;


public class RecordEntityField extends AbstractEntityField {






    protected String field;

    protected Class<?> type;




    RecordEntityField(String field, Class<?> type) {
        super();
        this.field = field;
        this.type = type;
    }

    @Override
    public Object getFieldValue(Object columnValue) {
        if (caster != null) {
            return caster.to(columnValue);
        }
        return columnValue;
    }

    @Override
    public Object get(Object target) {
        return ((DataObject) target).get(field);
    }

    @Override
    public String getFieldName() {
        return field;
    }

    @Override
    public void set(Object data, Object fieldValue) {
        ((DataObject) data).put(field, fieldValue);
    }

    /**
     * 如果是Record基本上就是数据库类型了
     *
     * @return
     */
    @Override
    public Type getFieldType() {
        return type;
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        RecordEntityField recordEntityField= new RecordEntityField(
                field,type
        );

        recordEntityField.validators = validators;
        recordEntityField.caster = caster;
        recordEntityField.statementAdapter = statementAdapter;
        recordEntityField.autoField = autoField;
        recordEntityField.columnMeta = columnMeta;
        recordEntityField.originalFieldName = originalFieldName;
        recordEntityField.column = column;
        recordEntityField.selectColumnName = selectColumnName;

        return recordEntityField;
    }
}
