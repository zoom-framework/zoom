package org.zoomdev.zoom.ioc.models;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.zoomdev.zoom.common.annotations.Inject;

import java.lang.annotation.Inherited;
import java.util.concurrent.CountDownLatch;

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

    @Inject
    private CountDownLatch countDownLatch;

    @Inject
    private UserService userService;
    /**
     * 都可以浏览
     */
    public String showProduct(){
        System.out.println("product is " + productService.getProduct());
        countDownLatch.countDown();
        return productService.getProduct();
    }


    public void editProduct(String info){
        String user = userService.getAdmin();
        productService.editProduct(user,info);
    }



}
