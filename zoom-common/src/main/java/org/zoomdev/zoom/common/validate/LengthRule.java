package org.zoomdev.zoom.common.validate;


import org.zoomdev.zoom.caster.Caster;

public class LengthRule<T> extends AbstractRule<T> {

    private int min;
    private int max;

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    LengthRule(String message, int min, int max) {
        super(message, "length");
        this.min = min;
        this.max = max;
    }

    //长度校验
    @Override
    public String validate(T data, Object value) {
        if (value == null) {
            //如果是空的，由其他规则限制，否则不做判断
            return null;
        }

        String str = Caster.to(value, String.class);

        if (str.length() >= min && str.length() <= max)
            return null;

        return message;
    }
}