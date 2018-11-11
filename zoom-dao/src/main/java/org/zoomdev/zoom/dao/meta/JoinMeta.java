package org.zoomdev.zoom.dao.meta;

public class JoinMeta {

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }


    private String table;

    private String on;

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }
}
