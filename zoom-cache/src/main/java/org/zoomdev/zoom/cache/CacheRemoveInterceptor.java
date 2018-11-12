package org.zoomdev.zoom.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.MethodInvoker;

import java.util.Arrays;

public class CacheRemoveInterceptor implements MethodInterceptor {
    private String format;
    private int count;
    private DataCache cache;

    private static final Log log = LogFactory.getLog(CacheRemoveInterceptor.class);

    public CacheRemoveInterceptor(String format, int count, DataCache cache) {
        this.format = format;
        this.count = count;
        this.cache = cache;
    }


    @Override
    public void intercept(MethodInvoker invoker) throws Throwable {
        invoker.invoke();

        final Object[] data = invoker.getArgs();
        final String key;
        final DataCache cache = this.cache;
        final String format = this.format;
        final int count = this.count;
        if (count == data.length) {
            key = String.format(format, data);
        } else {
            key = String.format(format, Arrays.copyOf(data, count));
        }
        if (log.isDebugEnabled()) {
            log.debug("删除缓存" + key);
        }
        cache.remove(key);
    }

}
