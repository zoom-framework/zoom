package org.zoomdev.zoom.dao.validator;

import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.dao.adapters.EntityField;

public class ValidatorException extends ZoomException {


    // 非空
    public static final int NULL = 1;

    // 太长
    public static final int LENGTH = 2;

    // 不能转换为数据库中的类型(比如 aaa不能转成数字)
    public static final int CAST = 3;

    private int type;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public EntityField getEntityField() {
        return entityField;
    }

    public void setEntityField(EntityField entityField) {
        this.entityField = entityField;
    }

    private Object value;

    private EntityField entityField;

    public ValidatorException(int type) {
        this.type = type;
    }


    public int getType() {
        return type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    @Override
    public String getMessage() {
        return message;
    }
}
