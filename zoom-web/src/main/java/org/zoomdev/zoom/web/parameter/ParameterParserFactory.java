package org.zoomdev.zoom.web.parameter;

import java.lang.reflect.Method;

/**
 * 参数解析器
 *
 * @author jzoom
 */
public interface ParameterParserFactory {
    /**
     * @param controllerClass
     * @param method
     * @param names
     * @return
     */
    ParameterParser createParamParser(
            Class<?> controllerClass,
            Method method,
            String[] names);


    void add(
            ParameterAdapterMaker maker
    );


}
