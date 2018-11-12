package org.zoomdev.zoom.common.filter.pattern;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.filter.Filter;

import java.util.regex.Pattern;

/**
 * 对字符串进行模式匹配,比如:    *.jar   jar.*   *\/controllers/*
 * 规则:    如果有逗号(,)，表示或匹配
 * 比如扫描jar的时候，确定哪些jar要扫描:    api-*.jar,xxx.jar
 * 支持如下：
 * 1、全部是*表示全部匹配
 * 2、*xxx  表示匹配xxx结尾
 * 3、xxx*   表示匹配xxx开头
 * 4、*xxx*  表示包含xxx
 * 5、*xxx*bbb  表示bbb结尾，包含xxx
 * 6、*xxx|*aaa   表示xxx结尾或者aaa结尾
 * 7、*xxx&aaa*	 表示xxx结尾并且aaa开头
 * 8、!*xxx 表示不是xxx结尾
 * 9、(*xxx|*aaa)&yyy* xxx结尾或者aaa结尾这两个条件有一个成立并且yyy开头...
 *
 * @author jzoom
 */
public class PatternFilter implements Filter<String> {

    private Pattern pattern;

    public PatternFilter(String filter) {
        assert (!StringUtils.isEmpty(filter));

        pattern = Pattern.compile(filter);
    }

    @Override
    public boolean accept(String value) {
        return pattern.matcher(value).matches();
    }

}
