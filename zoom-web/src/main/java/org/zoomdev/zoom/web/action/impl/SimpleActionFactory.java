package org.zoomdev.zoom.web.action.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.aop.impl.ReflectMethodCaller;
import org.zoomdev.zoom.aop.reflect.ClassInfo;
import org.zoomdev.zoom.common.ConfigurationConstants;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.utils.CollectionUtils;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.web.action.Action;
import org.zoomdev.zoom.web.action.ActionFactory;
import org.zoomdev.zoom.web.action.ActionInterceptorFactory;
import org.zoomdev.zoom.web.annotations.Param;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.ParameterParserFactory;
import org.zoomdev.zoom.web.parameter.PreParameterParserManager;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.rendering.RenderingFactory;
import org.zoomdev.zoom.web.rendering.RenderingFactoryManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SimpleActionFactory implements ActionFactory {

    @Inject
    protected IocContainer ioc;

    @Inject
    protected ClassInfo classInfo;

    @Inject(config = ConfigurationConstants.SERVER_ENCODING)
    protected String encoding;

    @Inject
    protected PreParameterParserManager preParameterParserManager;


    @Inject
    private ParameterParserFactory parameterParserFactory;


    @Inject
    private RenderingFactoryManager renderingFactory;



    public SimpleActionFactory() {

    }


    protected Rendering createRendering(Class<?> targetClass, Method method) {
        return renderingFactory.createRendering(targetClass, method);
    }

    protected ParameterParser createParameterParser(Class<?> controllerClass, Method method, String[] names) {
        return parameterParserFactory.createParamParser(controllerClass, method, names);
    }


    protected Rendering createErrorRendering(Class<?> controllerClass, Method method) {
        return renderingFactory.createRendering(controllerClass, method);
    }

    /**
     * 这里如果Param的配置了的话
     *
     * @param controllerClass
     * @param method
     * @return
     */
    private String[] getNames(Class<?> controllerClass, Method method) {
        String[] names = classInfo.getParameterNames(controllerClass, method);
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int index = 0;
        final Filter filter = new Filter<Annotation>() {
            @Override
            public boolean accept(Annotation value) {
                return value instanceof Param;
            }
        };
        for (String name : names) {
            if (name == null) {
                throw new RuntimeException("获取名称失败");
            }
            Annotation[] annotations = parameterAnnotations[index];
            Param param = (Param)CollectionUtils.get(annotations, filter);
            if(param!=null){
                if (param.name().startsWith("{") && param.name().endsWith("}")) {
                    String pathName = param.name()
                            .substring(1, param.name().length() - 1);
                    names[index] = pathName;
                    break;
                } else {
                    if (!StringUtils.isEmpty(param.name())) {
                        names[index] = param.name();
                    }
                }
            }

            ++index;
        }
        return names;
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

    @Override
    public Action createAction(Object target, Class<?> controllerClass, Method method,
                               ActionInterceptorFactory actionInterceptorFactory) {
        if (encoding == null) {
            encoding = "utf-8";
        }
        Action action = new Action();
        action.setEncoding(encoding);
        action.setIoc(getIoc());
        action.setMethod(method);

        String[] names = getNames(controllerClass, method);

        action.setPathVariableNames(getPathVariableNames(method, names));
        action.setCaller(new ReflectMethodCaller(method));
        action.setTarget(target);
        action.setParameterNames(names);

        action.setActionInterceptors(actionInterceptorFactory.create(controllerClass, method));
        action.setPreParamParser(preParameterParserManager);
        action.setParamParser(createParameterParser(controllerClass, method, names));
        action.setRendering(createRendering(controllerClass, method));
        action.setErrorRendering(createErrorRendering(controllerClass, method));

        return action;
    }

    public IocContainer getIoc() {
        return ioc;
    }




}
