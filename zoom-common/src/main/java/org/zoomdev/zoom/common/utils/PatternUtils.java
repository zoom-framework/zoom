package org.zoomdev.zoom.common.utils;

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
     * 使用Pattern对字符串访问
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
}
