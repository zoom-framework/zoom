package org.zoomdev.zoom.ioc.models.impl;

import org.zoomdev.zoom.ioc.models.PushService;

public class PushServiceImpl implements PushService {

    private String key;

    public PushServiceImpl(
            String key
    ){
        this.key = key;
    }


}
