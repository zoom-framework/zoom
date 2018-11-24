package org.zoomdev.zoom.cache.oscache;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import org.zoomdev.zoom.cache.DataCache;

import java.util.concurrent.TimeUnit;

public class OSCache implements DataCache {

    private GeneralCacheAdministrator cache;

    public OSCache() {
        cache = new GeneralCacheAdministrator();
    }

    @Override
    public Object get(String key) {
        try {
            return cache.getFromCache(key);
        } catch (NeedsRefreshException e) {
            cache.cancelUpdate(key);
            return null;
        }
    }

    @Override
    public Object set(String key, Object value) {

        cache.putInCache(key, value);

        return value;
    }

    @Override
    public Object set(String key, Object value, TimeUnit unit, int timeout) {
        return set(key, value, (int) unit.toMillis(timeout));
    }

    public static class OSEntryRefreshPolicy implements EntryRefreshPolicy {

        long now;

        long timeoutMs;

        OSEntryRefreshPolicy(long timeoutMs) {
            this.now = System.currentTimeMillis();
            this.timeoutMs = timeoutMs;
        }

        @Override
        public boolean needsRefresh(CacheEntry cacheEntry) {
            return System.currentTimeMillis() - cacheEntry.getLastUpdate() > timeoutMs;
        }
    }

    @Override
    public Object set(String key, Object value, int timeoutMs) {
        cache.putInCache(key, value, new OSEntryRefreshPolicy(timeoutMs));
        return value;
    }

    @Override
    public void remove(String key) {
        cache.removeEntry(key);
    }
}
