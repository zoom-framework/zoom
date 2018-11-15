package org.zoomdev.zoom.dao.auto;


import org.zoomdev.zoom.dao.adapters.EntityField;

public class DatabaseAutoGenerateKey implements AutoField {


    public DatabaseAutoGenerateKey() {
    }

    @Override
    public boolean notGenerateWhenHasValue() {
        return false;
    }

    @Override
    public boolean isDatabaseGeneratedKey() {
        return true;
    }

    @Override
    public String getSqlInsert(Object entity, EntityField entityField) {
        return null;
    }

    @Override
    public Object generateValue(Object entity, EntityField entityField) {
        return null;
    }
}