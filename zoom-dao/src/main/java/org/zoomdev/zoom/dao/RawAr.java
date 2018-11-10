package org.zoomdev.zoom.dao;

public interface RawAr extends Ar {


    /**
     *
     * @param arr
     * @return
     */
    Ar tables(String[] arr);

    /**
     * @param table
     * @return
     */
    Ar table(String table);




    /**
     * dao.ar().table("xxx").set("id",1).set("name","123").insertOrUpdate("id"),当id存在则更新，否则插入
     * @param keys
     * @return
     */
    int insertOrUpdate(String...keys);
}
