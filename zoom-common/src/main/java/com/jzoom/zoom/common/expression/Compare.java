package com.jzoom.zoom.common.expression;

/**
 * 用于运算符号的比较接口
 */
public class Compare {

    public static interface CompareValue {
        boolean compare(Comparable value1, Comparable value2);
    }

    public static CompareValue GT = new CompareValue() {
        @Override
        public boolean compare(Comparable value1, Comparable value2) {
            return value1.compareTo(value2) > 0;
        }
    };

    public static CompareValue GTE = new CompareValue() {
        @Override
        public boolean compare(Comparable value1, Comparable value2) {
            return value1.compareTo(value2) >= 0;
        }
    };

    public static CompareValue LT = new CompareValue() {
        @Override
        public boolean compare(Comparable value1, Comparable value2) {
            return value1.compareTo(value2) < 0;
        }
    };


    public static CompareValue LTE = new CompareValue() {
        @Override
        public boolean compare(Comparable value1, Comparable value2) {
            return value1.compareTo(value2) <= 0;
        }
    };

    public static CompareValue EQ = new CompareValue() {
        @Override
        public boolean compare(Comparable value1, Comparable value2) {
            return value1.compareTo(value2) == 0;
        }
    };
    public static CompareValue NEQ = new CompareValue() {
        @Override
        public boolean compare(Comparable value1, Comparable value2) {
            return value1.compareTo(value2) != 0;
        }
    };
}
