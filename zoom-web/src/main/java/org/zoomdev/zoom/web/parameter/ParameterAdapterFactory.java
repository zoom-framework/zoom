package org.zoomdev.zoom.web.parameter;

import org.zoomdev.zoom.web.action.ActionContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * 对上一步骤preParse解析结果的基础上，创建对应到调用Method参数的适配器工厂。
 */
public interface ParameterAdapterFactory {

    boolean shouldAdapt(ActionContext context);


    ParameterAdapter createParameterAdapter(
            String name,
            Type type,
            Annotation[] annotations
    );

    void addAdapterMaker(ParameterAdapterMaker factory);

}
