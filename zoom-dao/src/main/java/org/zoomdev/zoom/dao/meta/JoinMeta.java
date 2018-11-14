package org.zoomdev.zoom.dao.meta;

import org.zoomdev.zoom.dao.SqlBuilder;

public class JoinMeta {
    public static JoinMeta create(String table, String on,String type){
        return new JoinMeta(table,on,type);
    }

    public static JoinMeta create(String table, String on){
        return new JoinMeta(table,on,SqlBuilder.INNER);
    }

    public JoinMeta(){

    }

    public JoinMeta(String table, String on, String type) {
        this.table = table;
        this.on = on;
        this.type = type;
    }

    private String table;
    private String on;
    private String type;


    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }




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
