package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.caster.ValueCaster;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.auto.AutoField;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.validator.Validator;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

abstract class AbstractEntityField implements EntityField , Cloneable {
    /// 数据从数据库取出来之后转化成实体类的字段类型
    protected ValueCaster caster;

    protected StatementAdapter statementAdapter;

    protected AutoField autoField;

    protected ColumnMeta columnMeta;

    protected String originalFieldName;

    protected String column;

    protected String selectColumnName;

    protected Validator[] validators;

    AbstractEntityField() {

    }

    public void setCaster(ValueCaster caster) {
        this.caster = caster;
    }

    public void setStatementAdapter(StatementAdapter statementAdapter) {
        this.statementAdapter = statementAdapter;
    }


    @Override
    public Object getFieldValue(Object columnValue) {
        return caster.to(columnValue);
    }

    @Override
    public String getColumnName() {
        return column;
    }

    @Override
    public String getOriginalFieldName() {
        return originalFieldName;
    }

    public void setOriginalFieldName(String originalFieldName) {
        this.originalFieldName = originalFieldName;
    }

    @Override
    public StatementAdapter getStatementAdapter() {
        return statementAdapter;
    }


    @Override
    public AutoField getAutoField() {
        return autoField;
    }

    @Override
    public void adapt(PreparedStatement ps, int index, Object value) throws SQLException {
        statementAdapter.adapt(ps, index, value);
    }

    public void setAutoField(AutoField autoField) {
        this.autoField = autoField;
    }

    @Override
    public String getSelectColumnName() {
        return selectColumnName;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public void setSelectColumnName(String selectColumnName) {
        this.selectColumnName = selectColumnName;
    }






    @Override
    public ColumnMeta getColumnMeta() {
        return columnMeta;
    }

    public void setColumnMeta(ColumnMeta columnMeta) {
        this.columnMeta = columnMeta;
    }



    @Override
    public Validator[] getValidators() {
        return validators;
    }

    public void setValidators(Validator[] validators) {
        this.validators = validators;
    }


}
