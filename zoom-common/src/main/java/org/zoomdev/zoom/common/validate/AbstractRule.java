package org.zoomdev.zoom.common.validate;

public abstract class AbstractRule<T> implements Validator<T> {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    protected String message;
    protected String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AbstractRule(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public AbstractRule() {

    }
}
