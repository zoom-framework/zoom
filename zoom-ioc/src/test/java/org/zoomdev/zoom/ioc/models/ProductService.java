package org.zoomdev.zoom.ioc.models;

import org.zoomdev.zoom.common.annotations.Inject;

import java.util.concurrent.CountDownLatch;

public class ProductService {

    @Inject
    private CountDownLatch countDownLatch;

    private String product;

    @Inject
    public void init(){
        product = "MyProduct";
    }

    public String getProduct() {
        countDownLatch.countDown();
        return product;
    }


    public void editProduct(String user,String info){
        this.product = info;
    }
}
