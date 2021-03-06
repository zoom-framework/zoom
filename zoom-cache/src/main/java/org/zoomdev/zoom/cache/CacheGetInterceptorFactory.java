package org.zoomdev.zoom.cache;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.factory.AnnotationMethodInterceptorFactory;
import org.zoomdev.zoom.cache.annotations.Cache;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.utils.Classes;

import java.lang.reflect.Method;
import java.util.List;

public class CacheGetInterceptorFactory extends AnnotationMethodInterceptorFactory<Cache> {

    private DataCache cache;

    public CacheGetInterceptorFactory(DataCache cache) {
        this.cache = cache;
    }


    @Override
    protected void createMethodInterceptors(Cache annotation, Method method, List<MethodInterceptor> interceptors) {
        String format = annotation.format();
        int count = Classes.getParameterCount(method);
        if (StringUtils.isEmpty(format)) {
            throw new ZoomException(method + "format不能为空");
        } else {
            int formatCount = StringUtils.countMatches(format, "%s");
            if (formatCount > count) {
                throw new ZoomException("%s的个数不能大于参数个数");
            }
        }
        int timeoutMs = annotation.timeoutMs();
        boolean fill = annotation.fill();
        interceptors.add(new CacheGetInterceptor(format, count, cache, fill, timeoutMs, annotation.ignoreNull(), annotation.lockWhenNull()));


    }

}
