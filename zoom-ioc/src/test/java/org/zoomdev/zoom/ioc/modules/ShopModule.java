package org.zoomdev.zoom.ioc.modules;

import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.ioc.models.PushService;

@Module
public class ShopModule {



    @IocBean
    public PushService getPushService(){

        return null;
    }
}
