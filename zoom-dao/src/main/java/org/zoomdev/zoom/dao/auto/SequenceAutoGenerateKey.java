package org.zoomdev.zoom.dao.auto;

import org.zoomdev.zoom.dao.adapters.EntityField;

public class SequenceAutoGenerateKey extends DatabaseAutoGenerateKey {

    private String sequenceName;

    public SequenceAutoGenerateKey(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    @Override
    public String getInsertPlaceHolder(Object entity, EntityField entityField) {
        return String.format("(SELECT %s.NEXTVAL FROM DUAL)", sequenceName);
    }
}