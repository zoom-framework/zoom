package org.zoomdev.zoom.common.validate;


import org.zoomdev.zoom.caster.Caster;

/**
 * 自然是只能对数字进行校验了
 *
 * @param <T>
 */
public class MaxMinRule<T> extends AbstractRule<T> {

    private Double max;
    private Double min;

    public MaxMinRule(String message, Object min, Object max) {
        super(message, "max");
        this.max = Caster.to(max, Double.class);
        this.min = Caster.to(min, Double.class);
    }

    @Override
    public String validate(T data, Object value) {
        if (value == null) {
            //如果是空的，由其他规则限制，否则不做判断
            return null;
        }
        Double target = Caster.to(value, Double.class);

        if (this.max != null && this.max.compareTo(target) < 0) {

            return message;

        }

        if (this.min != null && this.min.compareTo(target) > 0) {
            return message;
        }

        return null;


    }
}