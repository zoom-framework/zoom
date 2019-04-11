package org.zoomdev.zoom.common.filter.pattern;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.filter.AlwaysAcceptFilter;
import org.zoomdev.zoom.common.filter.AndFilter;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.filter.OrFilter;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.CollectionUtils;
import org.zoomdev.zoom.common.utils.StrKit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串模式匹配工厂
 * {@see PatternFilter}
 *
 * @author jzoom
 */
public class PatternFilterFactory {

    public static class PatternException extends ZoomException {

        public PatternException(String message) {
            super(message);
        }

    }

    private static Map<String, Filter<String>> filterMap = new ConcurrentHashMap<String, Filter<String>>();

    /**
     * 这个用来匹配 *xxx*   *xxx   xxx* 的形式
     */
    private static Pattern SIMPLE_PATTERN = Pattern.compile("(\\**)([^\\*]+)(\\**)");

    /**
     * 用来替换字符串中的*
     */
    private static String EXP = "[a-zA-Z0-9_\\.\\/\\+\\-]*";

    public static void clear() {
        Classes.destroy(filterMap);
    }

    public static Filter<String> createFilter(String pattern) {

        if (StringUtils.isEmpty(pattern)) {
            return new AlwaysAcceptFilter<String>();
        }

        Filter<String> filter = filterMap.get(pattern);
        if (filter == null) {
            filter = createGroup(pattern);
            filterMap.put(pattern, filter);
        }
        return filter;

    }

    private static Filter<String> createGroup(String pattern) {
        char[] chars = pattern.toCharArray();
        StrReader reader = new StrReader(chars);
        return read(reader, new ReadContext(), false);
    }

    private static class StrReader {
        char[] chars;
        int index = 0;

        public String left() {
            return new String(chars, index, chars.length - index);
        }

        public StrReader(char[] chars) {
            this.chars = chars;
        }

        public char read() {
            if (index >= chars.length) {
                return '\0';
            }
            return chars[index++];
        }


        public String readPattern() {
            int endIndex = chars.length - 1;
            for (int i = index; i < chars.length; ++i) {
                char c = chars[i];
                if (c == '(' || c == ')' || c == '!' || c == '&' || c == '|') {
                    endIndex = i - 1;
                    break;
                }
            }

            String str = new String(chars, index - 1, endIndex - index + 2);
            index = endIndex + 1;
            //System.out.println(str);
            return str;

        }

        public String formatError(int index) {
            if (index > 0) {
                return new String(chars, 0, index - 1)
                        + ">>>" + chars[index - 1] + "<<<"
                        + new String(chars, index, chars.length - index);
            }


            return new String(chars, 0, index) + ":" + new String(chars, index, chars.length - index);

        }
    }


    static class ReadContext {
        //左括号位置
        List<Integer> brackets = new ArrayList<Integer>();
        //brackets

        public void addLeftBrackets(int index) {
            brackets.add(index);
        }

        public void removeRightBrackets(int index, StrReader reader) {
            if (brackets.size() == 0) {
                throw new PatternException("非法的右括号" + reader.formatError(index));
            }
            brackets.remove(brackets.size() - 1);
        }

    }

    @SuppressWarnings("unchecked")
    private static Filter<String> read(StrReader chars, ReadContext context, boolean quick) {
        List<Filter<String>> list = new ArrayList<Filter<String>>();
        Filter<String> filter;

        WHILE:
        while (true) {
            char c = chars.read();
            if (c == '\0') {
                break;
            }
            switch (c) {
                case '(': {
                    context.addLeftBrackets(chars.index);
                    filter = read(chars, context, false);
                    break;
                }

                case ')':
                    //back
                    context.removeRightBrackets(chars.index, chars);
                    break WHILE;
                case '!': {
                    filter = new NotFilter<String>(read(chars, context, true));
                    break;
                }
                case '&': {
                    if (list.size() == 0) {
                        return read(chars, context, true);
                    }
                    Filter<String> left = list.remove(list.size() - 1);
                    filter = new AndFilter<String>(left, read(chars, context, true));
                    break;
                }

                case '|': {
                    filter = read(chars, context, false);
                    break;
                }
                default: {
                    filter = create(chars.readPattern());
                    break;
                }

            }

            if (quick) {
                return filter;
            }
            list.add(filter);
        }


        if (list.size() == 0) {
            /// not possible
            //throw new RuntimeException();


            if (context.brackets.size() > 0) {

                throw new PatternException("非法的左括号" + chars.formatError(CollectionUtils.last(context.brackets)));
            }
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        return new OrFilter<String>(list.toArray(new Filter[list.size()]));

    }

    private static Filter<String> create(String pattern) {
        if (StrKit.isAll(pattern, '*')) {
            return new AlwaysAcceptFilter<String>();
        }

        //* 替换为[a-zA-Z\\.\\/]*

        Matcher matcher = SIMPLE_PATTERN.matcher(pattern);
        if (matcher.matches()) {

            if (StringUtils.isEmpty(matcher.group(1))) {
                //空的   xxx* 或者 xxx
                if (StringUtils.isEmpty(matcher.group(3))) {
                    //只有非*?
                    return new ExactFilter(matcher.group(2));
                } else {
                    //只有非*?
                    return new StartsWithFilter(matcher.group(2));
                }
            } else {
                if (StringUtils.isEmpty(matcher.group(3))) {
                    return new EndsWithFilter(matcher.group(2));
                } else {
                    return new ContainsFilter(matcher.group(2));
                }

            }

        }
        pattern = pattern.replace(".", "\\.");
        pattern = pattern.replace("*", EXP);

        return new PatternFilter(pattern);
    }


}
