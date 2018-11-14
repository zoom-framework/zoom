package org.zoomdev.zoom.common.validate;


import org.zoomdev.zoom.common.expression.Symbol;

import java.util.Map;

public class MapCompareRule extends CompareRule<Map<String, Object>> {
    public MapCompareRule(String message, String target, Symbol symbol) {
        super(message, target, symbol);
    }

    public MapCompareRule() {

    }

    @Override
    protected Object get(Map<String, Object> data, String target) {
        return data.get(target);
    }
}


