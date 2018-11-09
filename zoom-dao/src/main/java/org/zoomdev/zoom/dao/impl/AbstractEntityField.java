package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.caster.ValueCaster;
import org.zoomdev.zoom.dao.AutoField;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

abstract class AbstractEntityField implements EntityField {
    /// 数据从数据库取出来之后转化成实体类的字段类型
    protected ValueCaster caster;

    public void setCaster(ValueCaster caster) {
        this.caster = caster;
    }

    public void setStatementAdapter(StatementAdapter statementAdapter) {
        this.statementAdapter = statementAdapter;
    }

    private StatementAdapter statementAdapter;

    private AutoField autoField;

    private String column;

    private String select;

    AbstractEntityField(String column, String select) {
        this.column = column;
        this.select = select;
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
    public String getSelectName() {
        return select;
    }

}
