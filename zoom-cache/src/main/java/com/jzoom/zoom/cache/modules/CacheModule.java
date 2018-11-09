package com.jzoom.zoom.cache.modules;

import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.cache.CacheGetInterceptorFactory;
import com.jzoom.zoom.cache.CacheRemoveInterceptorFactory;
import com.jzoom.zoom.cache.DataCache;
import com.jzoom.zoom.cache.annotations.CacheEnable;
import com.jzoom.zoom.cache.chcache.EhDataCache;
import com.jzoom.zoom.common.annotations.Inject;
import com.jzoom.zoom.common.annotations.IocBean;
import com.jzoom.zoom.common.annotations.Module;

@Module(CacheEnable.class)
public class CacheModule {

	
	public CacheModule() {
		
	}
	
	
	@IocBean
	public DataCache getCache() {
		return new EhDataCache();
	}

	
	@Inject
	public void init(AopFactory factory,DataCache dataCache) {
		factory.methodInterceptorFactory(new CacheGetInterceptorFactory(dataCache), 0);
		factory.methodInterceptorFactory(new CacheRemoveInterceptorFactory(dataCache), 0);
	}
	
}
