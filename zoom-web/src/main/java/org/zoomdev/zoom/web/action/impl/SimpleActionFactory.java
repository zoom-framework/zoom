package org.zoomdev.zoom.web.action.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.aop.impl.ReflectMethodCaller;
import org.zoomdev.zoom.aop.reflect.ClassInfo;
import org.zoomdev.zoom.common.ConfigurationConstants;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.web.action.Action;
import org.zoomdev.zoom.web.action.ActionFactory;
import org.zoomdev.zoom.web.action.ActionInterceptorFactory;
import org.zoomdev.zoom.web.annotations.Param;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.ParameterParserFactory;
import org.zoomdev.zoom.web.parameter.PreParameterParser;
import org.zoomdev.zoom.web.parameter.PreParameterParserFactory;
import org.zoomdev.zoom.web.parameter.parser.impl.AbsParameterParserFactory;
import org.zoomdev.zoom.web.parameter.parser.impl.SimpleParameterParserFactory;
import org.zoomdev.zoom.web.parameter.pre.impl.SimplePreParameterParserFactory;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.rendering.RenderingFactory;
import org.zoomdev.zoom.web.rendering.impl.BeetlRendering;
import org.zoomdev.zoom.web.rendering.impl.SimpleErrorRenderingFactory;
import org.zoomdev.zoom.web.rendering.impl.SimpleRenderingFactory;
import org.zoomdev.zoom.web.rendering.impl.TemplateRendering;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class SimpleActionFactory implements ActionFactory {

    @Inject
    protected IocContainer ioc;

    @Inject
    protected ClassInfo classInfo;

    @Inject(config = ConfigurationConstants.SERVER_ENCODING)
    protected String encoding;

    private ParameterParserFactory parameterParserFactory;
    private RenderingFactory renderingFactory;
    private RenderingFactory errorRenderingFactory;
    private PreParameterParserFactory preParameterParserFactory;

    public SimpleActionFactory() {

    }

    protected Rendering createRendering(Class<?> targetClass, Method method) {
        return getRenderingFactory().createRendering(targetClass, method);
    }

    protected ParameterParser createParameterParser(Class<?> controllerClass, Method method, String[] names) {
        return getParameterParserFactory().createParamParser(controllerClass, method, names);
    }

    protected PreParameterParser createPreParameterParser(Class<?> controllerClass, Method method) {
        return getPreParameterParserFactory().createPreParameterParser(controllerClass, method);
    }

    protected Rendering createErrorRendering(Class<?> controllerClass, Method method) {
        return getErrorRenderingFactory().createRendering(controllerClass, method);
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
        for (String name : names) {
            if (name == null) {
                throw new RuntimeException("获取名称失败");
            }

            Annotation[] annotations = parameterAnnotations[index];

            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    Param param = (Param) annotation;
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

        action.setPathVariableNames(AbsParameterParserFactory.getPathVariableNames(method, names));
        action.setCaller(new ReflectMethodCaller(method));
        action.setTarget(target);

        action.setActionInterceptors(actionInterceptorFactory.create(controllerClass, method));
        action.setPreParamParser(createPreParameterParser(controllerClass, method));
        action.setParamParser(createParameterParser(controllerClass, method, names));
        action.setRendering(createRendering(controllerClass, method));
        action.setErrorRendering(createErrorRendering(controllerClass, method));

        return action;
    }

    public IocContainer getIoc() {
        return ioc;
    }

    public ParameterParserFactory getParameterParserFactory() {
        if (parameterParserFactory == null) {
            parameterParserFactory = new SimpleParameterParserFactory();
        }
        return parameterParserFactory;
    }

    public void setParameterParserFactory(ParameterParserFactory parameterParserFactory) {
        this.parameterParserFactory = parameterParserFactory;
    }

    public RenderingFactory getRenderingFactory() {
        if (errorRenderingFactory == null) {
            renderingFactory = new SimpleRenderingFactory(createTemplateRendering());
        }
        return renderingFactory;
    }

    private TemplateRendering createTemplateRendering() {
        return BeetlRendering.createFileRendering();
    }

    public void setRenderingFactory(RenderingFactory renderingFactory) {
        this.renderingFactory = renderingFactory;
    }

    public RenderingFactory getErrorRenderingFactory() {
        if (errorRenderingFactory == null)
            errorRenderingFactory = new SimpleErrorRenderingFactory(createTemplateRendering());
        return errorRenderingFactory;
    }

    public void setErrorRenderingFactory(RenderingFactory errorRenderingFactory) {
        this.errorRenderingFactory = errorRenderingFactory;
    }

    public PreParameterParserFactory getPreParameterParserFactory() {
        if (preParameterParserFactory == null)
            preParameterParserFactory = new SimplePreParameterParserFactory();
        return preParameterParserFactory;
    }

    public void setPreParameterParserFactory(PreParameterParserFactory preParameterParserFactory) {
        this.preParameterParserFactory = preParameterParserFactory;
    }

}
