package org.zoomdev.zoom.dao.validator;

import org.zoomdev.zoom.common.exceptions.ZoomException;

public class ValidatorException extends ZoomException {


    // 非空
    public static final int NULL = 1;

    // 太长
    public static final int LENGTH = 2;

    // 不能转换为数据库中的类型(比如 aaa不能转成数字)
    public static final int CAST = 3;

    private int type;


    public ValidatorException(int type) {
        this.type = type;
    }


    public int getType() {
        return type;
    }
}
