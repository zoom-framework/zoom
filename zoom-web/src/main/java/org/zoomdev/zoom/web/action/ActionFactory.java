package org.zoomdev.zoom.web.action;

import java.lang.reflect.Method;

/**
 * Action 工厂
 *
 * @author jzoom
 */
public interface ActionFactory {

    Action createAction(Object target,
                        Class<?> controllerClass,
                        Method method,
                        ActionInterceptorFactory factory);
}
