package org.zoomdev.zoom.web.action.impl;

import org.zoomdev.zoom.common.filter.ClassAndMethodFilter;
import org.zoomdev.zoom.common.filter.impl.PatternClassAndMethodFilter;
import org.zoomdev.zoom.common.utils.OrderedList;
import org.zoomdev.zoom.web.action.ActionInterceptor;
import org.zoomdev.zoom.web.action.ActionInterceptorFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SimpleActionInterceptorFactory implements ActionInterceptorFactory {

    private OrderedList<InterceptorInfo> list;
    private InterceptorInfo[] interceptors;

    private static class InterceptorInfo {
        InterceptorInfo(ActionInterceptor interceptor, ClassAndMethodFilter filter) {
            this.interceptor = interceptor;
            this.filter = filter;
        }

        ActionInterceptor interceptor;
        ClassAndMethodFilter filter;
    }

    public SimpleActionInterceptorFactory() {
        list = new OrderedList<InterceptorInfo>();
    }


    @Override
    public void add(ActionInterceptor interceptor, String pattern, int order) {
        list.add(new InterceptorInfo(interceptor, new PatternClassAndMethodFilter(pattern)), order);
    }


    @Override
    public void add(ActionInterceptor interceptor, ClassAndMethodFilter filter, int order) {
        list.add(new InterceptorInfo(interceptor, filter), order);
    }


    @Override
    public ActionInterceptor[] create(Class<?> controllerClass, Method method) {

        List<ActionInterceptor> tmp = new ArrayList<ActionInterceptor>();

        InterceptorInfo[] interceptors = this.interceptors;
        tmp.clear();

        if (interceptors == null) {
            interceptors = list.toArray(new InterceptorInfo[list.size()]);
        }
        for (InterceptorInfo interceptorInfo : interceptors) {
            if (interceptorInfo.filter.accept(controllerClass) && interceptorInfo.filter.accept(controllerClass, method)) {
                tmp.add(interceptorInfo.interceptor);
            }
        }

        if (tmp.size() == 0) {
            return null;
        }


        return tmp.toArray(new ActionInterceptor[tmp.size()]);
    }


}
