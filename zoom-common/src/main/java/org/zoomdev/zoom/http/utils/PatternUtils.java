package org.zoomdev.zoom.http.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {


    public static interface PatternVisitor {
        /**
         * 符合条件的字符串
         *
         * @param matcher
         */
        void onGetPattern(Matcher matcher);

        /**
         * 剩余字符串
         *
         * @param rest
         */
        void onGetRest(String rest);
    }

    /**
     * 使用Pattern对字符串访问,将访问所有匹配和未匹配的字符
     *
     * @param target
     * @param pattern
     * @return
     */
    public static void visit(String target, Pattern pattern, PatternVisitor visitor) {
        assert (target != null && pattern != null);
        Matcher matcher = pattern.matcher(target);
        int start = 0;
        while (matcher.find()) {
            visitor.onGetRest(target.substring(start, matcher.start()));
            visitor.onGetPattern(matcher);
            start = matcher.end();
        }

        visitor.onGetRest(target.substring(start));
    }


    public static final Pattern INT_PATTERN = Pattern.compile("^[+]?\\d+$");
    public static final Pattern FLOAT_PATTERN = Pattern.compile("^[-\\+]?\\d+(\\.\\d+)?$");

    /**
     * 是否是整数
     *
     * @param value
     * @return
     */
    public static boolean isInteger(String value) {
        if (value == null) return false;
        return INT_PATTERN.matcher(value).matches();
    }

    /**
     * 是否是浮点
     *
     * @param value
     * @return
     */
    public static boolean isFloat(String value) {
        if (value == null) return false;
        return FLOAT_PATTERN.matcher(value).matches();
    }

    public static boolean isNumber(String value) {
        if (value == null) return false;
        return isInteger(value) || isFloat(value);
    }

}
