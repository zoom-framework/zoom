package org.zoomdev.zoom.dao.validator;

import org.zoomdev.zoom.http.utils.PatternUtils;

public class IntegerValidator implements Validator {

    public static final Validator DEFAULT = new IntegerValidator();

    public static boolean isInteger(Object value) {
        if (value instanceof Integer
                || value instanceof Short
                || value instanceof Byte
                || value instanceof Long) {

            return true;
        }
        return false;
    }

    @Override
    public void validate(Object value) {

        if (value == null) {
            return;
        }


        if (isInteger(value)) {
            return;
        }

        if (value instanceof String) {
            if (!PatternUtils.isInteger((String) value)) {
                throw new ValidatorException(ValidatorException.CAST);
            }
            return;
        }

        if (value instanceof Number) {
            return;
        }

        if (value instanceof Boolean) {
            return;
        }

        //其他格式都不能转
        throw new ValidatorException(ValidatorException.CAST);

    }
}
