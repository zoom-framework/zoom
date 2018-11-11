package org.zoomdev.zoom.dao.meta;

import org.zoomdev.zoom.common.expression.Symbol;

public class OnMeta {
    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    private String first;
    private String second;
    private Symbol symbol;

}
