package org.zoomdev.zoom.web.action.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.aop.impl.ReflectMethodCaller;
import org.zoomdev.zoom.aop.reflect.ClassInfo;
import org.zoomdev.zoom.http.ConfigurationConstants;
import org.zoomdev.zoom.http.annotations.Inject;
import org.zoomdev.zoom.http.filter.Filter;
import org.zoomdev.zoom.http.utils.CollectionUtils;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.web.action.Action;
import org.zoomdev.zoom.web.action.ActionFactory;
import org.zoomdev.zoom.web.action.ActionInterceptorFactory;
import org.zoomdev.zoom.web.annotations.Param;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.ParameterParserFactory;
import org.zoomdev.zoom.web.parameter.PreParameterParserManager;
import org.zoomdev.zoom.web.rendering.RenderingChain;
import org.zoomdev.zoom.web.rendering.RenderingFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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
    private RenderingFactory renderingFactory;



    public SimpleActionFactory() {

    }



    public void setIoc(IocContainer ioc) {
        this.ioc = ioc;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public PreParameterParserManager getPreParameterParserManager() {
        return preParameterParserManager;
    }

    public void setPreParameterParserManager(PreParameterParserManager preParameterParserManager) {
        this.preParameterParserManager = preParameterParserManager;
    }

    public ParameterParserFactory getParameterParserFactory() {
        return parameterParserFactory;
    }

    public void setParameterParserFactory(ParameterParserFactory parameterParserFactory) {
        this.parameterParserFactory = parameterParserFactory;
    }

    public RenderingFactory getRenderingFactory() {
        return renderingFactory;
    }

    public void setRenderingFactory(RenderingFactory renderingFactory) {
        this.renderingFactory = renderingFactory;
    }

    protected RenderingChain createRendering(Class<?> targetClass, Method method) {
        return renderingFactory.createRendering(targetClass, method);
    }

    protected ParameterParser createParameterParser(Class<?> controllerClass, Method method, String[] names) {
        return parameterParserFactory.createParamParser(controllerClass, method, names);
    }


    protected RenderingChain createErrorRendering(Class<?> controllerClass, Method method) {
        return renderingFactory.createExceptionRendering(controllerClass, method);
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
