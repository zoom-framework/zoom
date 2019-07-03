package org.zoomdev.zoom.web.action.impl;

import org.zoomdev.zoom.aop.impl.ReflectMethodCaller;
import org.zoomdev.zoom.common.ConfigurationConstants;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.web.action.Action;
import org.zoomdev.zoom.web.action.ActionFactory;
import org.zoomdev.zoom.web.action.ActionInterceptorFactory;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.ParameterParserFactory;
import org.zoomdev.zoom.web.parameter.PreParameterParserManager;
import org.zoomdev.zoom.web.rendering.RenderingChain;
import org.zoomdev.zoom.web.rendering.RenderingFactory;

import java.lang.reflect.Method;

public class SimpleActionFactory implements ActionFactory {


    public SimpleActionFactory() {

    }


    @Inject
    protected PreParameterParserManager preParameterParserManager;


    @Inject
    private ParameterParserFactory parameterParserFactory;


    @Inject
    private RenderingFactory renderingFactory;

    @Inject
    private ActionInterceptorFactory actionInterceptorFactory;


    @Inject(config = ConfigurationConstants.SERVER_ENCODING)
    protected String encoding;

    @Inject
    protected IocContainer ioc;


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


    @Override
    public Action createAction(ActionHolder holder) {
        if (encoding == null) {
            encoding = "utf-8";
        }
        Action action = new Action();
        action.setEncoding(encoding);
        action.setIoc(getIoc());
        action.setMethod(holder.getMethod());

        String[] names = holder.getNames();

        action.setCaller(new ReflectMethodCaller(holder.getMethod()));
        action.setTarget(holder.getTarget());
        action.setParameterNames(names);


        Class<?> controllerClass = holder.getControllerClass();
        Method method = holder.getMethod();

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
