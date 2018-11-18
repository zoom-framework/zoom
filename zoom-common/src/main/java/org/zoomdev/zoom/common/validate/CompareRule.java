package org.zoomdev.zoom.common.validate;

import org.zoomdev.zoom.common.expression.Symbol;

/**
 * 这个涉及两个字段比较，需要从数据中取出对应的值
 *
 * @param <T>
 */
public abstract class CompareRule<T> extends AbstractRule<T> {

    //比较目标的key
    private String target;


    public CompareRule() {

    }

    public CompareRule(String message, String target, Symbol symbol) {
        super(message, "compare");
        this.target = target;
        this.symbol = symbol;
    }


    // 比较运算符
    private Symbol symbol;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }


    @Override
    public String validate(T data, Object value) {
        if (!this.symbol.compare((Comparable) value, (Comparable) get(data, target))) {
            return message;
        }
        return null;
    }

    protected abstract Object get(T data, String target);

}