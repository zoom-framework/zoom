package org.zoomdev.zoom.common.expression;

/**
 * 比较运算符号，用于解析数据库或参数中的运算符
 *
 * @author jzoom
 */
public enum Symbol implements Compare.CompareValue {
    GT(">", Compare.GT),                //>
    LT("<", Compare.LT),                //<
    GTE(">=", Compare.GTE),                //>=
    LTE("<=", Compare.LTE),                //<=
    NEQ("<>", Compare.NEQ),                //<>
    EQ("=", Compare.EQ);                //=

    private String value;
    private Compare.CompareValue compareValue;

    Symbol(String value, Compare.CompareValue compareValue) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    /**
     * 比较关系
     *
     * @param value1
     * @param value2
     * @return
     */
    public boolean compare(Comparable value1, Comparable value2) {
        return compareValue.compare(value1, value2);
    }

    public static Symbol parse(String value) {
        if (">".equals(value)) {
            return GT;
        }
        if ("<".equals(value)) {
            return LT;
        }
        if (">=".equals(value)) {
            return GTE;
        }
        if ("<=".equals(value)) {
            return LTE;
        }
        if ("<>".equals(value)) {
            return NEQ;
        }
        if ("=".equals(value)) {
            return EQ;
        }
        return null;
    }
}
