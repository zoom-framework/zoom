package org.zoomdev.zoom.common.validate;

import java.io.Serializable;

public abstract class AbstractRule<T> implements Validator<T>, Serializable {

    protected String message;
    protected String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AbstractRule(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public AbstractRule() {

    }


}
