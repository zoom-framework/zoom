package org.zoomdev.zoom.dao.entities;

import org.zoomdev.zoom.dao.annotations.Column;
import org.zoomdev.zoom.dao.annotations.Table;

/**
 * 统计商品的平均价格（按商店)
 */
@Table("shp_product")
public class GroupByEntity {


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Column("avg(pro_price)")
    private double price;




}
