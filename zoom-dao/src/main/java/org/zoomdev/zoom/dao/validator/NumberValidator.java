package org.zoomdev.zoom.dao.validator;


import org.zoomdev.zoom.http.utils.PatternUtils;

public class NumberValidator implements Validator {

    public static final Validator DEFAULT = new IntegerValidator();

    public static boolean isNumber(Object value) {
        if (value instanceof Number) {

            return true;
        }
        return false;
    }

    @Override
    public void validate(Object value) {

        if (value == null) {
            return;
        }


        if (isNumber(value)) {
            return;
        }

        if (value instanceof String) {
            if (!PatternUtils.isNumber((String) value)) {
                throw new ValidatorException(ValidatorException.CAST);
            }
        }


        if (value instanceof Boolean) {
            return;
        }


        //其他格式都不能转
        throw new ValidatorException(ValidatorException.CAST);

    }
}
