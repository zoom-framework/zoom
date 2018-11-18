package org.zoomdev.zoom.dao.validator;

public class NotNullValidator implements Validator {


    @Override
    public void validate(Object value) {

        if (value == null) {
            throw new ValidatorException(ValidatorException.NULL);
        }

    }
}
