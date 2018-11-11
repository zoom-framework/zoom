package org.zoomdev.zoom.dao.meta;

public class JoinMeta {

    private String type;

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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
