package com.jzoom.zoom.ioc;

import com.jzoom.zoom.common.annotations.Inject;

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
