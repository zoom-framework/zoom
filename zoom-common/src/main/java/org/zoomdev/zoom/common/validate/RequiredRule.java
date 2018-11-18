package org.zoomdev.zoom.common.validate;

public class RequiredRule<T> extends AbstractRule<T> {

    public RequiredRule(String message){
        super(message,"required");
    }

    @Override
    public String validate(T data, Object value) {
        if(value == null){
            return message;
        }
        return null;
    }
}
