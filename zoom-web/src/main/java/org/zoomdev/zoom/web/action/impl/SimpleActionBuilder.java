package org.zoomdev.zoom.web.action.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.filter.impl.ClassAnnotationFilter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilterFactory;
import org.zoomdev.zoom.common.res.ClassResolver;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.web.action.Action;
import org.zoomdev.zoom.web.action.ActionFactory;
import org.zoomdev.zoom.web.action.ActionInterceptorFactory;
import org.zoomdev.zoom.web.annotations.Controller;
import org.zoomdev.zoom.web.annotations.Mapping;
import org.zoomdev.zoom.web.annotations.Template;
import org.zoomdev.zoom.web.router.Router;

import java.lang.reflect.Method;
import java.util.List;

public class SimpleActionBuilder extends ClassResolver {

    private static final Log log = LogFactory.getLog(SimpleActionBuilder.class);

    private IocContainer ioc;

    private ActionInterceptorFactory factory;

    private Router router;

    private Class<? extends ActionFactory> defaultActionFactoryClass = SimpleActionFactory.class;


    private Class<?> clazz;
    private Controller controller;
    private Object target;
    private String key;

    public List<Router.RemoveToken> getRemoveTokenList() {
        return removeTokenList;
    }

    private List<Router.RemoveToken> removeTokenList;

    public SimpleActionBuilder(IocContainer ioc, Router router) {
        this(ioc, router, null);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public SimpleActionBuilder(IocContainer ioc, Router router, List<Router.RemoveToken> removeTokenList) {
        setClassFilter(new ClassAnnotationFilter(Controller.class));
        setClassNameFilter(PatternFilterFactory.createFilter("*.controllers.*"));

        this.ioc = ioc;
        this.router = router;
        ioc.getIocClassLoader().append(ActionInterceptorFactory.class, new SimpleActionInterceptorFactory(), true);
        factory = ioc.fetch(ActionInterceptorFactory.class);
        this.removeTokenList = removeTokenList;
    }


    @Override
    public void clear() {
        target = null;
        clazz = null;
        key = null;
        controller = null;
    }

    @Override
    public void visitClass(Class<?> clazz) {
        this.clazz = clazz;
        target = ioc.fetch(clazz);
        controller = clazz.getAnnotation(Controller.class);
        key = controller.key();
    }

    protected String getKey(String key, Method method, Mapping mapping) {
        if (mapping != null) {
            if (!mapping.value().startsWith("/") && !key.endsWith("/") && !mapping.value().isEmpty()) {
                key += "/" + mapping.value();

            } else {
                key += mapping.value();
            }

        } else {
            if (!key.endsWith("/")) {
                key += "/" + method.getName();
            } else {
                key += method.getName();
            }
        }
        if (!key.startsWith("/")) {
            key = "/" + key;
        }
        return key;
    }


    @Override
    public void visitMethod(Method method) {
        Class<? extends ActionFactory> actionFactoryClass = defaultActionFactoryClass;
        ActionFactory factory = ioc.fetch(actionFactoryClass);
        Action action = factory.createAction(target, clazz, method, this.factory);
        Mapping mapping = method.getAnnotation(Mapping.class);
        String key = getKey(this.key, method, mapping);
        action.setUrl(key);
        String[] methods;
        if (mapping != null) {
            methods = mapping.method();
        } else {
            methods = null;
        }
        action.setHttpMethods(methods);
        //模板路径
        Template template = method.getAnnotation(Template.class);
        if (template != null) {
            action.setPath(template.path());
        } else {
            action.setPath(key.substring(1));
        }


        if (log.isInfoEnabled()) {
            log.info(String.format("注册Action成功:key:[%s] class:[%s] method:[%s] loader:[%s]", key, clazz, method.getName(), clazz.getClassLoader()));
        }
        Router.RemoveToken removeToken = router.register(key, action);
        if (removeTokenList != null) {
            removeTokenList.add(removeToken);
        }


    }

    @Override
    public boolean resolveFields() {
        return false;
    }

    @Override
    public boolean resolveMethods() {
        return true;
    }


}
