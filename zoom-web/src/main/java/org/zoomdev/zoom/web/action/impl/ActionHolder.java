package org.zoomdev.zoom.web.action.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.utils.CollectionUtils;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.IocObject;
import org.zoomdev.zoom.ioc.impl.ZoomIocKey;
import org.zoomdev.zoom.web.action.Action;
import org.zoomdev.zoom.web.action.ActionFactory;
import org.zoomdev.zoom.web.action.ActionHandler;
import org.zoomdev.zoom.web.action.ActionInterceptorFactory;
import org.zoomdev.zoom.web.annotations.Mapping;
import org.zoomdev.zoom.web.annotations.Param;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ActionHolder implements ActionHandler {

    public ActionHolder(Class<?> controllerClass, Method method, IocContainer ioc, String key) {
        this.controllerClass = controllerClass;
        this.method = method;
        this.ioc = ioc;
        this.key = key;
    }

    private Class<?> controllerClass;
    private Method method;
    private IocContainer ioc;
    private String key;
    private static  final Log log = LogFactory.getLog(ActionHolder.class);

    /// lazyload
    private Action action;


    public void visitMethod() {
        ioc.waitFor();
        IocObject target = ioc.fetch(new ZoomIocKey(controllerClass));
        ActionFactory actionFactory = ioc.fetch(ActionFactory.class);
        ActionInterceptorFactory actionInterceptorFactory = ioc.fetch(ActionInterceptorFactory.class);
        Action action = actionFactory.createAction(target.get(), controllerClass, method, actionInterceptorFactory);
        Mapping mapping = method.getAnnotation(Mapping.class);
        String[] methods;
        if (mapping != null) {
            methods = mapping.method();
        } else {
            methods = null;
        }

        action.setUrl(key);
        //模板路径
        action.setPath(key.substring(1));
        if (log.isDebugEnabled()) {
            log.debug(String.format("注册Action成功:key:[%s] class:[%s] method:[%s] loader:[%s]",
                    key,
                    controllerClass,
                    method.getName(),
                    controllerClass.getClassLoader()));
        }
//        Router.RemoveToken removeToken = router.register(key, action);
//        if (removeTokenList != null) {
//            removeTokenList.add(removeToken);
//        }

        this.action = action;

    }
    public static String[] getPathVariableNames(Method method, String[] names) {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        int c = names.length;
        List<String> pathVariableNames = new ArrayList<String>();
        final Filter<Annotation> filter = new Filter<Annotation>() {
            @Override
            public boolean accept(Annotation value) {
                return value instanceof Param;
            }
        };
        for (int i = 0; i < c; ++i) {
            Annotation[] annotations = paramAnnotations[i];
            Param param = (Param) CollectionUtils.get(annotations, filter);
            if (param != null) {
                if (param.name().startsWith("{") && param.name().endsWith("}")) {
                    String pathName = param.name()
                            .substring(1, param.name().length() - 1);
                    pathVariableNames.add(pathName);
                } else if (param.pathVariable()) {
                    pathVariableNames.add(StringUtils.isEmpty(param.name()) ? names[i] : param.name());
                }
            }
        }

        return CollectionUtils.toArray(pathVariableNames);
    }

    private Action lazyLoad(){
        synchronized (this){
            if(action==null){
                visitMethod();
            }
            return action;
        }
    }

    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        Action action = this.action;
        if(action==null){
            action = lazyLoad();
        }
        return action.handle(request,response);
    }
    private String[] methods;

    public void setHttpMethods(String[] methods) {
        this.methods = methods;
    }


    public String[] getMethods() {
        return methods;
    }
    @Override
    public boolean supportsHttpMethod(String method) {
        if (this.methods != null) {
            for (String m : methods) {
                if (m.equals(method)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private String[] names;

    @Override
    public String[] getPathVariableNames() {
        if(names==null){
            Action action = this.action;
            if(action==null){
                action = lazyLoad();
            }
            names = getPathVariableNames(method,action.getParameterNames());
        }
        return names;
    }
}
