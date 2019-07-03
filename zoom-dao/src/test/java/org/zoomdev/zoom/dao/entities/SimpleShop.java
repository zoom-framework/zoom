package org.zoomdev.zoom.dao.entities;

import org.zoomdev.zoom.dao.annotations.Table;

@Table("shop")
public class SimpleShop {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String id;

    private String title;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;


}
