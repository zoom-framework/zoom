package org.zoomdev.zoom.dao.entities;

import org.zoomdev.zoom.dao.annotations.AutoGenerate;
import org.zoomdev.zoom.dao.annotations.Table;

@Table("shp_product")
public class SimpleProduct {


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

    @AutoGenerate
    private int id;

    private String name;

    private double price;

    String thumb;

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    byte[] img;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    String createdAt;


    String info;

    public int getTpId() {
        return tpId;
    }

    public void setTpId(int tpId) {
        this.tpId = tpId;
    }

    int tpId;

}
