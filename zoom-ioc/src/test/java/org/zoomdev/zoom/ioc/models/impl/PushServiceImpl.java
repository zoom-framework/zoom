package org.zoomdev.zoom.ioc.models.impl;

import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.ioc.annotations.TestListener;
import org.zoomdev.zoom.ioc.models.PushService;
import org.zoomdev.zoom.ioc.models.ShopService;

public class PushServiceImpl implements PushService {


    /**
     * 模拟循环依赖
     */
    @Inject
    private ShopService shopService;

    private String key;

    public PushServiceImpl(
            String key
    ){
        this.key = key;
    }



    public void send(){

        System.out.println( shopService.getName() + "Notify");

    }

    @Override
    public String getName() {
        return shopService.getName();
    }



    public void init(){
    }


    public void destroy(){

    }

    @TestListener
    public void notifyMessage(){

        System.out.println("on notify");
    }
}
