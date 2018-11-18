package org.zoomdev.zoom.plugin.impl;

import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.annotations.ApplicationModule;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.configuration.SimpleConfigBuilder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class PluginConfigBuilder extends SimpleConfigBuilder {

    public PluginConfigBuilder(IocContainer ioc) {
        super(ioc);
    }

    @Override
    public void endResolve() {
        //初始化application
        List<Class<?>> types = new ArrayList<Class<?>>();
        for (Class<?> type : list) {
            if (type.isAnnotationPresent(ApplicationModule.class)) {
                continue;
            }
            Module module = type.getAnnotation(Module.class);
            Class<? extends Annotation> annotationClass = module.value();
            if (annotationClass == Module.class) {
                types.add(type);
            } else {
                log.info("没有找到对应的标注:" + annotationClass + " 模块" + type + "未启用,若要启用模块，请使用ApplicationModule标注模块，并使用响应的Enable标注");
            }
        }

        for (Class<?> type : types) {
            ioc.getIocClassLoader().appendModule(type);
        }

        for (Class<?> type : types) {
            ioc.fetch(type);
        }

        list.clear();
    }
}
