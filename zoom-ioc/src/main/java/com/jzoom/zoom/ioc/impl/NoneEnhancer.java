package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.ioc.ClassEnhancer;

public class NoneEnhancer implements ClassEnhancer {
    @Override
    public Class<?> enhance(Class<?> target) {
        return target;
    }
}
