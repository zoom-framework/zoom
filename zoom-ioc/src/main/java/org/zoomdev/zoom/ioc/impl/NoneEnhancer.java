package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.ClassEnhancer;

public class NoneEnhancer implements ClassEnhancer {
    @Override
    public Class<?> enhance(Class<?> target) {
        return target;
    }
}
