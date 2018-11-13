package org.zoomdev.zoom.dao.entities;

import org.zoomdev.zoom.dao.DataAdapters;
import org.zoomdev.zoom.dao.annotations.AutoGenerate;
import org.zoomdev.zoom.dao.annotations.Column;
import org.zoomdev.zoom.dao.annotations.Table;

@Table("shp_product")
public class SimpleProduct {


    @AutoGenerate
    private int id;
    private String name;
    private double price;
    @Column(adapter = DataAdapters.TimeStamp2String.class)
    private String createdAt;
    private int tpId;
    private String info;
    private byte[] img;
    private String thumb;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }



    public int getTpId() {
        return tpId;
    }

    public void setTpId(int tpId) {
        this.tpId = tpId;
    }



}
