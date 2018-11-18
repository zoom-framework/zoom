package org.zoomdev.zoom.dao.entities;

import org.zoomdev.zoom.dao.annotations.Column;
import org.zoomdev.zoom.dao.annotations.Join;
import org.zoomdev.zoom.dao.annotations.Link;
import org.zoomdev.zoom.dao.annotations.Table;

@Link({@Join(table = "type", on = "tpId=typeId")})
@Table("product")
public class JoinProductWithType {


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

    private int id;

    private String name;

    private double price;

    String thumb;


    String info;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    // 类型名称
    @Column("type.tp_title")
    String typeName;


}
