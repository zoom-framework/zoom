package org.zoomdev.zoom.ioc.models;

import org.zoomdev.zoom.common.annotations.Inject;

/**
 *
 * 逻辑： 商店销售商品，当商品售出之后推送到商户那边
 *
 * 修改商品需要登录权限
 *
 */
public class ShopService {


    @Inject
    private ProductService productService;




}
