package org.zoomdev.zoom.cache.modules;

import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.cache.CacheGetInterceptorFactory;
import org.zoomdev.zoom.cache.CacheRemoveInterceptorFactory;
import org.zoomdev.zoom.cache.DataCache;
import org.zoomdev.zoom.cache.annotations.CacheEnable;
import org.zoomdev.zoom.cache.chcache.EhDataCache;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;

@Module(CacheEnable.class)
public class CacheModule {


    public CacheModule() {

    }


    @IocBean
    public DataCache getCache() {
        return new EhDataCache();
    }


    @Inject
    public void init(AopFactory factory, DataCache dataCache) {
        factory.addMethodInterceptorFactory(new CacheGetInterceptorFactory(dataCache), 0);
        factory.addMethodInterceptorFactory(new CacheRemoveInterceptorFactory(dataCache), 0);
    }

}
