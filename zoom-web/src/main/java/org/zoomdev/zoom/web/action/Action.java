package org.zoomdev.zoom.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.aop.MethodCaller;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.PreParameterParserManager;
import org.zoomdev.zoom.web.rendering.RenderingChain;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


public class Action implements Destroyable {


    /**
     * 渲染器
     */
    private RenderingChain rendering;

    /**
     * 异常渲染器
     */
    private RenderingChain errorRendering;

    /**
     * 对参数进行预处理,如将整个request读取一个json
     */
    private PreParameterParserManager preParamParser;

    /**
     * 将预处理的参数解析成可调用方法的参数列表
     */
    private ParameterParser paramParser;

    /**
     * 指的是一个controller的引用
     */
    private Object target;


    /**
     * 原始方法
     */
    private Method method;

    /**
     * 编码
     */
    private String encoding;

    /**
     * id
     */
    private String id;

    /**
     * 方法调用
     */
    private MethodCaller caller;

    /**
     * ioc容器
     */
    private IocContainer ioc;

    /**
     * {@link org.zoomdev.zoom.web.annotations.Mapping}
     * <p>
     * 最原始的映射url
     */
    private String url;

    /**
     * interceptor
     */
    private ActionInterceptor[] actionInterceptors;

    /**
     * path为视图的path，需要根据url解析
     */
    private String path;


    private static final Log log = LogFactory.getLog(Action.class);


    private String[] parameterNames;

    /**
     * 对于一个singleton的aciton，target为controller，
     * 如果不为singleton则每个request都会创建一个controller controller可以重用,可以被缓存
     */
    public Action() {

    }

    public String getMapping() {
        return url;
    }

    @Override
    public void destroy() {
        if (actionInterceptors != null) {
            Classes.destroy(actionInterceptors);
            actionInterceptors = null;
        }

        if (caller != null) {
            Classes.destroy(caller);
            caller = null;
        }

        if (errorRendering != null) {
            Classes.destroy(errorRendering);
            errorRendering = null;
        }

        if (rendering != null) {
            Classes.destroy(rendering);
            rendering = null;
        }

        if (paramParser != null) {
            Classes.destroy(paramParser);
            paramParser = null;
        }

        if (preParamParser != null) {
            Classes.destroy(preParamParser);
            preParamParser = null;
        }
    }


    public void release(ActionContext context) throws Exception {
        ioc.release(IocContainer.Scope.REQUEST);


        /// 寿终正寝,完成了光荣的使命，可以销毁了
        context.destroy();
    }


    public void handle(ActionContext context) throws ServletException {
        try {
            process(context);
        } catch (Throwable e) {
            // 错误处理
            context.setException(e);
            handlerError(context);
        } finally {
            // 这里可以释放资源等操作
            context.setState(ActionContext.STATE_AFTER_RENDER);


            if (this.actionInterceptors != null) {
                for (ActionInterceptor actionInterceptor : actionInterceptors) {
                    try {
                        actionInterceptor.complete(context);
                    } catch (Exception e) {
                        log.fatal("在调用拦截器的complete的时候发生异常", e);
                    }
                }
            }
            try {
                release(context);
            } catch (Exception e) {
                //这里如果有异常,那么只是记录一下
                log.fatal("在release阶段发生异常", e);
            }
        }
    }

    /**
     * 渲染异常
     *
     * @param context
     */
    public void handlerError(ActionContext context) throws ServletException {
        context.setState(ActionContext.STATE_BEFORE_RENDER);
        if (this.actionInterceptors != null) {
            for (ActionInterceptor actionInterceptor : actionInterceptors) {
                try {
                    if (!actionInterceptor.whenError(context)) {
                        return;
                    }
                } catch (Exception e) {
                    throw new ZoomException("调用拦截器的whenError发生异常", e);
                }
            }
        }
        try {
            errorRendering.render(context);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }


    /**
     * 设置编码
     *
     * @param context
     * @throws Exception
     */
    private void setEncoding(ActionContext context) throws Exception {
        context.getRequest().setCharacterEncoding(encoding);
        context.getResponse().setCharacterEncoding(encoding);
        context.setState(ActionContext.STATE_PRE_PARSE);
    }

    /**
     * 参数解析，比如读取json对象
     *
     * @param context
     * @throws Exception
     */
    private void preParse(ActionContext context) throws Exception {
        if (context.getState() == ActionContext.STATE_PRE_PARSE) {
            if (actionInterceptors != null) {
                for (ActionInterceptor actionInterceptor : actionInterceptors) {
                    if (context.getState() == ActionContext.STATE_PRE_PARSE) {
                        if (!actionInterceptor.preParse(context)) {
                            //结束switch
                            context.setState(ActionContext.STATE_AFTER_RENDER);
                            return;
                        }
                    }

                }
            }
            if (context.getState() == ActionContext.STATE_PRE_PARSE) {
                Object data = preParamParser.preParse(context);
                context.setPreParam(data);
            }

        }
    }


    /**
     * 渲染
     *
     * @param context
     * @throws Exception
     */
    private void render(ActionContext context) throws Exception {

        if (context.getState() == ActionContext.STATE_BEFORE_RENDER) {
            rendering.render(context);
        }
    }

    /**
     * 解析参数，将上一步解析出来的结果转为方法调用的参数
     *
     * @param context
     * @throws Exception
     */
    private void parse(ActionContext context) throws Throwable {


        if (context.getState() == ActionContext.STATE_PARSE) {
            if (actionInterceptors != null) {
                for (ActionInterceptor actionInterceptor : actionInterceptors) {
                    if (context.getState() == ActionContext.STATE_PARSE) {
                        actionInterceptor.parse(context);
                    }
                }
            }
            if (context.getState() == ActionContext.STATE_PARSE) {
                Object[] args = paramParser.parse(context);
                context.setArgs(args);
            }

        }
    }

    /**
     * 调用
     *
     * @param context
     * @throws Exception
     */
    private void invoke(ActionContext context) throws Exception {
        if (context.getState() == ActionContext.STATE_BEFORE_INVOKE) {
            Object result = caller.invoke(context.getTarget(), context.getArgs());
            context.setResult(result);
            if (actionInterceptors != null) {
                for (ActionInterceptor actionInterceptor : actionInterceptors) {
                    if (context.getState() == ActionContext.STATE_BEFORE_RENDER) {
                        actionInterceptor.whenResult(context);
                    }
                }
            }
        }

    }

    /**
     * 在各个时期，都可以使用切面截取
     *
     * @param context
     * @throws Exception
     */
    public void process(ActionContext context) throws Throwable {

        switch (context.getState()) {

            case ActionContext.STATE_INIT:
                setEncoding(context);

            case ActionContext.STATE_PRE_PARSE:

                preParse(context);

            case ActionContext.STATE_PARSE:
                parse(context);

            case ActionContext.STATE_BEFORE_INVOKE:
                invoke(context);

            case ActionContext.STATE_BEFORE_RENDER:
                render(context);
        }

    }


    public void setPreParamParser(PreParameterParserManager preParamParser) {
        this.preParamParser = preParamParser;
    }

    public ParameterParser getParamParser() {
        return paramParser;
    }

    public void setParamParser(ParameterParser paramParser) {
        this.paramParser = paramParser;
    }

    public RenderingChain getRendering() {
        return rendering;
    }

    public void setRendering(RenderingChain rendering) {
        this.rendering = rendering;
    }

    public RenderingChain getErrorRendering() {
        return errorRendering;
    }

    public void setErrorRendering(RenderingChain errorRendering) {
        this.errorRendering = errorRendering;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }


    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getId() {
        return id;
    }


    public String getPath() {
        return path;
    }

    public void setId(String id) {
        this.id = id;
    }


    public MethodCaller getCaller() {
        return caller;
    }


    public void setCaller(MethodCaller caller) {
        this.caller = caller;
    }


    public IocContainer getIoc() {
        return ioc;
    }


    public void setIoc(IocContainer ioc) {
        this.ioc = ioc;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public ActionInterceptor[] getActionInterceptors() {
        return actionInterceptors;
    }


    public void setActionInterceptors(ActionInterceptor[] actionInterceptors) {
        this.actionInterceptors = actionInterceptors;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ActionContext context = new ActionContext(request, response, this);
        context.setTarget(target);
        handle(context);

        return true;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(String[] parameterNames) {
        this.parameterNames = parameterNames;
    }

    public Object getTarget() {
        return target;
    }


    public void setTarget(Object target) {
        this.target = target;
    }


    @Override
    public String toString() {
        return new StringBuilder().append("Action: url:").append(url).toString();
    }


}
