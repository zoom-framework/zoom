package org.zoomdev.zoom.web.action.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.aop.reflect.ClassInfo;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.utils.CollectionUtils;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.web.action.Action;
import org.zoomdev.zoom.web.action.ActionFactory;
import org.zoomdev.zoom.web.action.ActionHandler;
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

    private static final Log log = LogFactory.getLog(ActionHolder.class);


    private Class<?> controllerClass;
    private Method method;
    private IocContainer ioc;
    private String key;


    private ClassInfo classInfo;

    /// lazyload
    private Action action;


    public ActionHolder(
            Class<?> controllerClass,
            Method method,
            IocContainer ioc,
            String key,
            ClassInfo classInfo) {
        this.controllerClass = controllerClass;
        this.method = method;
        this.ioc = ioc;
        this.key = key;
        this.classInfo = classInfo;
    }


    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public IocContainer getIoc() {
        return ioc;
    }

    public void setIoc(IocContainer ioc) {
        this.ioc = ioc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setMethods(String[] methods) {
        this.methods = methods;
    }

    private String[] names;

    public String[] getNames() {
        if (names == null) {
            initNames();
        }
        return names;
    }


    public void visitMethod() {
        ioc.waitFor();
        ActionFactory actionFactory = ioc.fetch(ActionFactory.class);
        Action action = actionFactory.createAction(this);
        Mapping mapping = method.getAnnotation(Mapping.class);
        String[] methods;
        if (mapping != null) {
            methods = mapping.method();
        } else {
            methods = null;
        }

        action.setUrl(key);
        //模板路径,如果带有{}的通配符，则将通配符删除掉
        action.setPath(key.substring(1).replace("{", "").replace("}", ""));
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

    static final Filter<Annotation> filter = new Filter<Annotation>() {
        @Override
        public boolean accept(Annotation value) {
            return value instanceof Param;
        }
    };


    private Action lazyLoad() {
        synchronized (this) {
            if (action == null) {
                visitMethod();
            }
            return action;
        }
    }

    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        Action action = this.action;
        if (action == null) {
            action = lazyLoad();
        }
        return action.handle(request, response);
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

    private String[] pathVariableNames;

    @Override
    public String[] getPathVariableNames() {
        if (pathVariableNames == null) {
            initNames();
        }
        return pathVariableNames;
    }

    private void initNames() {
        String[] names = classInfo.getParameterNames(controllerClass, method);
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        int c = names.length;
        List<String> pathVariableNames = new ArrayList<String>();

        for (int i = 0; i < c; ++i) {
            Annotation[] annotations = paramAnnotations[i];
            Param param = (Param) CollectionUtils.get(annotations, filter);
            if (param != null) {
                if (param.name().startsWith("{") && param.name().endsWith("}")) {
                    String pathName = param.name()
                            .substring(1, param.name().length() - 1);
                    pathVariableNames.add(pathName);
                    names[i] = pathName;
                } else if (param.pathVariable()) {
                    String pathName = StringUtils.isEmpty(param.name()) ? names[i] : param.name();
                    pathVariableNames.add(pathName);
                    names[i] = pathName;
                }
            }
        }

        this.pathVariableNames = CollectionUtils.toArray(pathVariableNames);
        this.names = names;
    }


    public Object getTarget() {
        return ioc.fetch(controllerClass);
    }
}
