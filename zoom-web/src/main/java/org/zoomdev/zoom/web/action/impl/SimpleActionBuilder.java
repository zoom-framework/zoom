package org.zoomdev.zoom.web.action.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.aop.reflect.ClassInfo;
import org.zoomdev.zoom.common.res.ClassResolver;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.web.annotations.Controller;
import org.zoomdev.zoom.web.annotations.Mapping;
import org.zoomdev.zoom.web.router.Router;

import java.lang.reflect.Method;
import java.util.List;

public class SimpleActionBuilder extends ClassResolver {

    private static final Log log = LogFactory.getLog(SimpleActionBuilder.class);

    private IocContainer ioc;


    private Router router;


    public List<Router.RemoveToken> getRemoveTokenList() {
        return removeTokenList;
    }

    private List<Router.RemoveToken> removeTokenList;

    private ClassInfo classInfo;

    public SimpleActionBuilder(IocContainer ioc, Router router, ClassInfo classInfo) {
        this(ioc, router, null, classInfo);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public SimpleActionBuilder(
            IocContainer ioc,
            Router router,
            List<Router.RemoveToken> removeTokenList, ClassInfo classInfo) {

        this.ioc = ioc;
        this.router = router;
        this.removeTokenList = removeTokenList;
        this.classInfo = classInfo;
    }

    @Override
    public void resolve(ResScanner scanner) {
        List<ResScanner.ClassRes> classes = scanner.findClass("*.controllers.*");
        for (ResScanner.ClassRes res : classes) {
            Class<?> controllerType = res.getType();
            Controller controller = controllerType.getAnnotation(Controller.class);
            if (controller == null) continue;
            String controllerKey = controller.key();
            Method[] methods = CachedClasses.getPublicMethods(controllerType);

            for (Method method : methods) {
                appendMethod(controllerType, method, controllerKey);
            }

        }
    }

    protected void appendMethod(Class<?> controllerType, Method method, String controllerKey) {
        Mapping mapping = method.getAnnotation(Mapping.class);
        String[] methods;
        if (mapping != null) {
            methods = mapping.method();
        } else {
            methods = null;
        }
        String key = getKey(controllerKey, method, mapping);
        log.info("映射Controller:" + key + "===>" + method);
        ActionHolder handler = new ActionHolder(
                controllerType, method, ioc, key, classInfo);
        handler.setHttpMethods(methods);
        Router.RemoveToken removeToken = router.register(key, handler);
        if (removeTokenList != null) {
            removeTokenList.add(removeToken);
        }
    }


    public static String getKey(String key, Method method, Mapping mapping) {
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


}
