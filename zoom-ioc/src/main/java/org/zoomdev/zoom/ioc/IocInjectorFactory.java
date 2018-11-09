package org.zoomdev.zoom.ioc;

import org.zoomdev.zoom.common.annotations.Inject;

import java.lang.reflect.Field;

public interface IocInjectorFactory {

    /**
     * 构建字段注入
     * @param inject
     * @param key
     * @param field
     * @return
     */
    IocField createIocField(Inject inject, IocKey key, Field field);
}
