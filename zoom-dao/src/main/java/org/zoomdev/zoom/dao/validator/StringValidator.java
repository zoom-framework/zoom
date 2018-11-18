package org.zoomdev.zoom.dao.validator;

public class StringValidator implements Validator {
    private int length;

    public StringValidator(int length) {
        this.length = length;
    }

    @Override
    public void validate(Object value) {
        if (value == null) {
            return;
        }

        String str = String.valueOf(value);
        if (str.length() > this.length) {
            throw new ValidatorException(ValidatorException.LENGTH);
        }
    }
}
