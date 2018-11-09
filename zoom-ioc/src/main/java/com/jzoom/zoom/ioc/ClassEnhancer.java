package com.jzoom.zoom.ioc;

/**
 * 类增强器
 */
public interface ClassEnhancer {

    /**
     * 返回增强后的类，为目标类的子类
     * @param target
     * @return
     */
    Class<?> enhance(Class<?> target);
}
