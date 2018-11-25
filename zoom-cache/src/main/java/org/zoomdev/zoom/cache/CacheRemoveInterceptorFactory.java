package org.zoomdev.zoom.cache;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.factory.AnnotationMethodInterceptorFactory;
import org.zoomdev.zoom.cache.annotations.CacheRemove;
import org.zoomdev.zoom.common.exceptions.ZoomException;

import java.lang.reflect.Method;
import java.util.List;

public class CacheRemoveInterceptorFactory extends AnnotationMethodInterceptorFactory<CacheRemove> {

    private DataCache cache;

    public CacheRemoveInterceptorFactory(DataCache cache) {
        this.cache = cache;
    }

    @Override
    protected void createMethodInterceptors(CacheRemove annotation, Method method, List<MethodInterceptor> interceptors) {
        String format = annotation.format();
        int count = method.getParameterTypes().length;
        if (StringUtils.isEmpty(format)) {
            throw new ZoomException(method+"format为空");
        } else {
            int formatCount = StringUtils.countMatches(format, "%s");
            if (formatCount < count) {
                throw new ZoomException("%s的个数不能小于参数个数");
            }
        }

        interceptors.add(new CacheRemoveInterceptor(format, count, cache));

    }

}
