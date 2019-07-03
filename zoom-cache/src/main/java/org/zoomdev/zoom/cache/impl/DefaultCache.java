package org.zoomdev.zoom.cache.impl;

import org.zoomdev.zoom.cache.DataCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DefaultCache implements DataCache {

    private static class CacheInfo {
        Object data;
        long time;
        long timeout;

        CacheInfo(Object data, long timeout) {
            time = System.currentTimeMillis();
            this.data = data;
            this.timeout = timeout;
        }

        boolean isTimeout() {
            return System.currentTimeMillis() - time > timeout;
        }
    }

    private Map<String, CacheInfo> map = new ConcurrentHashMap<String, CacheInfo>();

    @Override
    public Object get(String key) {
        CacheInfo cacheInfo = map.get(key);
        if (cacheInfo == null || cacheInfo.isTimeout()) {
            return null;
        }

        return cacheInfo.data;
    }

    @Override
    public Object set(String key, Object value) {
        return set(key, value, 20 * 1000 * 60);
    }

    @Override
    public Object set(String key, Object value, TimeUnit unit, int timeout) {
        return set(key, value, unit.toMillis(timeout));
    }

    @Override
    public Object set(String key, Object value, long timeoutMs) {
        return map.put(key, new CacheInfo(value, timeoutMs));
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }
}
