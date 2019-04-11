
package org.zoomdev.zoom.cache.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.cache.DataCache;
import org.zoomdev.zoom.http.annotations.Inject;
import org.zoomdev.zoom.http.res.ResScanner;
import org.zoomdev.zoom.http.res.ResScanner.Res;

import java.util.concurrent.TimeUnit;


public class EhDataCache implements DataCache {
    private CacheManager cacheManager;
    private static final Log log = LogFactory.getLog(EhDataCache.class);

    public static final String CACHE_NAME = "global";

    @Inject
    public void init(ResScanner scanner) {
        Res res = scanner.getFile("ehcache.xml");
        if (res == null) {
            log.warn("没有找到ehcache的配置文件ehcache.xml,不使用缓存");
            return;
        }
        // 1. 创建缓存管理器
        cacheManager = CacheManager.create(res.getFile().getPath());
        log.info("============================缓存系统启动====================================");

    }


    @Override
    public Object get(String key) {
        if (cacheManager == null) return null;
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            throw new RuntimeException("未配置缓存" + CACHE_NAME);
        }
        Element value = cache.get(key);
        if (value == null)
            return null;
        return value.getObjectValue();
    }

    @Override
    public Object set(String key, Object value) {
        if (cacheManager == null)
            return value;
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            throw new RuntimeException("未配置缓存" + CACHE_NAME);
        }
        cache.put(new Element(key, value));
        return value;
    }

    @Override
    public Object set(String key, Object value, TimeUnit unit, int timeout) {

        return set(key,value, (int) unit.toMillis(timeout));
    }

    @Override
    public Object set(String key, Object value, int timeoutMs) {
        if (cacheManager == null)
            return value;
        Cache cache = cacheManager.getCache(CACHE_NAME);
        cache.put(new Element(key, value,timeoutMs/1000,timeoutMs/1000));
        return value;
    }

    @Override
    public void remove(String key) {
        if (cacheManager == null)
            return;
        Cache cache = cacheManager.getCache(CACHE_NAME);
        cache.remove(key);
    }

}