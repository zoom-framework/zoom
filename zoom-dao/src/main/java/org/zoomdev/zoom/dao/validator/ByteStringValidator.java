package org.zoomdev.zoom.dao.validator;

public class ByteStringValidator implements Validator {
    private int length;

    public ByteStringValidator(int length) {
        this.length = length;
    }


    @Override
    public void validate(Object value) {
        if (value == null) {
            return;
        }

        if (value instanceof byte[]) {
            if (((byte[]) value).length > this.length) {
                throw new ValidatorException(ValidatorException.LENGTH);
            }
        }

        String str = String.valueOf(value);
        if (str.getBytes().length > this.length) {
            throw new ValidatorException(ValidatorException.LENGTH);
        }

    }
}
