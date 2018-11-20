package org.zoomdev.zoom.web.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


/**
 * 用户自定义
 */
public interface ParameterAdapterMaker {


    ParameterAdapter createParameterAdapter(
            String name,
            Type type,
            Annotation[] annotations
    );
}
