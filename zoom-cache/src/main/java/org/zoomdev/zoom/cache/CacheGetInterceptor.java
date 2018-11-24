package org.zoomdev.zoom.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.MethodInvoker;
import org.zoomdev.zoom.common.lock.LockUtils;

import java.util.Arrays;

public class CacheGetInterceptor implements MethodInterceptor {

    private String format;
    private int count;
    private boolean fill;
    private DataCache cache;
    private int timeoutMs;
    private boolean ignoreNull;

    private boolean lockWhenNull;

    private static final Log log = LogFactory.getLog(CacheGetInterceptor.class);

    public CacheGetInterceptor(String format,
                               int count,
                               DataCache cache,
                               boolean fill,
                               int timeoutMs,
                               boolean ignoreNull,
                               boolean lockWhenNull) {
        this.format = format;
        this.count = count;
        this.cache = cache;
        this.fill = fill;
        this.ignoreNull = ignoreNull;
        this.timeoutMs = timeoutMs;
        this.lockWhenNull = lockWhenNull;
    }

    @Override
    public void intercept(MethodInvoker invoker) throws Throwable {
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
            log.debug("尝试从缓存取出数据" + key);
        }
        Object value = cache.get(key);
        if (value == null) {
            //cache
            if (lockWhenNull) {
                synchronized (LockUtils.getLock(key)) {
                    invokeAndSetCache(invoker, key);
                }
            } else {
                invokeAndSetCache(invoker, key);
            }

        } else {
            if (log.isDebugEnabled()) {
                log.debug("缓存获取成功" + key);
            }
            invoker.setReturnObject(value);
            invoker.invoke();
        }
    }

    private void invokeAndSetCache(MethodInvoker invoker, String key) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("缓存没有获取到" + key);
        }
        invoker.invoke();
        Object value = invoker.getReturnObject();

        if ((value == null && ignoreNull) || !fill) {
            return;
        }

        cache.set(key, value, timeoutMs);
    }

}
