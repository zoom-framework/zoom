package org.zoomdev.zoom.common.validate;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.zoomdev.zoom.caster.Caster;

import java.util.regex.Pattern;

public class PatternRule<T> extends AbstractRule<T> {
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    private String pattern;

    @JsonIgnore
    private Pattern _pattern;

    public PatternRule(String message, String pattern) {
        super(message, "pattern");
        this.pattern = pattern;
    }

    public PatternRule() {

    }


    @Override
    public String validate(T data, Object value) {
        if (value == null) {
            //如果是空的，由其他规则限制，否则不做判断
            return null;
        }
        String str = Caster.to(value, String.class);
        Pattern _pattern = this._pattern;
        if (_pattern == null) {
            _pattern = Pattern.compile(pattern);
            this._pattern = _pattern;
        }
        if (_pattern.matcher(str).matches()) {
            return null;
        }
        return message;
    }
}

