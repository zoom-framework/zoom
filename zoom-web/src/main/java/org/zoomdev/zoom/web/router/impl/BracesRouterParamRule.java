package org.zoomdev.zoom.web.router.impl;

import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.web.router.RouterParamRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析大括号的模式
 * <p>
 * /a/{b}/{c}
 *
 * @author jzoom
 */
public class BracesRouterParamRule implements RouterParamRule {

    private static final Pattern PATTERN = Pattern.compile("\\{([^\\}]+)\\}");

    private static final Pattern CORRECT_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");

    public static final String ERROR = "只能以字母开头，含有字母数字下划线";

    @Override
    public String getParamName(String value) {
        Matcher matcher = PATTERN.matcher(value);
        if (matcher.matches()) {
            String realValue = matcher.group(1);
            if (!CORRECT_PATTERN.matcher(realValue).matches()) {
                throw new ZoomException(ERROR);
            }
            return realValue;
        }
        return null;
    }

    @Override
    public boolean match(String url) {
        return url.contains("{");
    }

}
