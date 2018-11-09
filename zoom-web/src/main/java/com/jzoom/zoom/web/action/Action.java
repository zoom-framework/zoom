package com.jzoom.zoom.web.action;

import com.jzoom.zoom.aop.MethodCaller;
import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.web.parameter.ParameterParser;
import com.jzoom.zoom.web.parameter.PreParameterParser;
import com.jzoom.zoom.web.rendering.Rendering;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


public class Action implements ActionHandler,Destroyable {

	
	/**
	 * 渲染器
	 */
	private Rendering rendering;

	/**
	 * 异常渲染器
	 */
	private Rendering errorRendering;

	/**
	 * 对参数进行预处理,如将整个request读取一个json
	 */
	private PreParameterParser preParamParser;

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
	 * 
	 * {@link com.jzoom.zoom.web.annotations.Mapping}
	 * 
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
	
	/**
	 * 对于一个singleton的aciton，target为controller，
	 * 如果不为singleton则每个request都会创建一个controller controller可以重用,可以被缓存
	 *
	 */
	public Action() {
		
	}
	@Override
	public String getMapping() {
		return url;
	}
	
	@Override
	public void destroy() {
		if(actionInterceptors!=null) {
			Classes.destroy(actionInterceptors);
			actionInterceptors = null;
		}
		
		if(caller != null) {
			Classes.destroy(caller);
			caller = null;
		}
		
		if(errorRendering !=null) {
			Classes.destroy(errorRendering);
			errorRendering = null;
		}
		
		if(rendering != null) {
			Classes.destroy(rendering);
			rendering = null;
		}
		
		if(paramParser != null) {
			Classes.destroy(paramParser);
			paramParser = null;
		}
		
		if(preParamParser!=null) {
			Classes.destroy(preParamParser);
			preParamParser = null;
		}
	}


	
	
	public void release(ActionContext context) throws Exception {
        ioc.release(IocContainer.Scope.REQUEST);
	}
	
	

	public void handle(ActionContext context) {
		try {
			process(context);
		} catch (Exception e) {
			// 错误处理
			context.setException(e);
			handlerError(context);
		} finally {
			// 这里可以释放资源等操作
			context.setState(ActionContext.STATE_AFTER_RENDER);
		
			
			if( this.actionInterceptors!=null ) {
				for (ActionInterceptor actionInterceptor : actionInterceptors) {
					try {
						actionInterceptor.complete(context);
					} catch (Exception e) {
						log.fatal("在调用拦截器的complete的时候发生异常",e);
					}
				}
			}
			try {
				release(context);
			} catch (Exception e) {
				//这里如果有异常,那么只是记录一下
				log.fatal("在release阶段发生异常",e);
			}
		}
	}

	/**
	 * 渲染异常
	 * @param context
	 */
	public void handlerError(ActionContext context) {
		context.setState(ActionContext.STATE_BEFORE_RENDER);
		if(this.actionInterceptors!=null) {
			for (ActionInterceptor actionInterceptor : actionInterceptors) {
				try {
					if(!actionInterceptor.whenError(context)) {
						return;
					}
				} catch (Exception e) {
					throw new RuntimeException("调用拦截器的whenError发生异常",e);
				}
			}
		}
		try {
			errorRendering.render(context);
		} catch (Exception e) {
			throw new RuntimeException("渲染错误发生异常",e);
		}
	}


	
	/**
	 * 设置编码
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
	 * @param context
	 * @throws Exception
	 */
	private void preParse(ActionContext context) throws Exception {
		if(actionInterceptors!=null) {
			for (ActionInterceptor actionInterceptor : actionInterceptors) {
				if(!actionInterceptor.preParse(context)) {
					//结束switch
					context.setState(ActionContext.STATE_AFTER_RENDER);
					return;
				}
			}
		}
		
		if(context.getState() == ActionContext.STATE_PRE_PARSE) {
			Object data = preParamParser.preParse(context);
			context.setPreParam(data);
		}
	}
	
	
	/**
	 * 渲染
	 * @param context
	 * @throws Exception 
	 */
	private void render(ActionContext context) throws Exception  {
		
		if(context.getState() == ActionContext.STATE_BEFORE_RENDER) {
			rendering.render(context);
		}
	}
	
	/**
	 * 解析参数，将上一步解析出来的结果转为方法调用的参数
	 * @param context
	 * @throws Exception
	 */
	private void parse(ActionContext context) throws Exception {
		if( actionInterceptors !=null ) {
			for (ActionInterceptor actionInterceptor : actionInterceptors) {
				if(context.getState() == ActionContext.STATE_PARSE) {
					actionInterceptor.parse(context);
				}
			}
		}
		
		if(context.getState() == ActionContext.STATE_PARSE) {
			Object[] args = paramParser.parse(context);
			context.setArgs(args);
		}
	}
	
	/**
	 * 调用
	 * @param context
	 * @throws Exception
	 */
	private void invoke(ActionContext context) throws Exception {
		Object result = caller.invoke(context.getTarget(), context.getArgs());
		context.setResult(result);
		if( actionInterceptors !=null ) {
			for (ActionInterceptor actionInterceptor : actionInterceptors) {
				if(context.getState() == ActionContext.STATE_BEFORE_RENDER) {
					actionInterceptor.whenResult(context);
				}
			}
		}
	}

	/**
	 * 在各个时期，都可以使用切面截取
	 * @param context
	 * @throws Exception
	 */
	public void process(ActionContext context) throws Exception {
		
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

	public PreParameterParser getPreParamParser() {
		return preParamParser;
	}

	public void setPreParamParser(PreParameterParser preParamParser) {
		this.preParamParser = preParamParser;
	}

	public ParameterParser getParamParser() {
		return paramParser;
	}

	public void setParamParser(ParameterParser paramParser) {
		this.paramParser = paramParser;
	}

	public Rendering getRendering() {
		return rendering;
	}

	public void setRendering(Rendering rendering) {
		this.rendering = rendering;
	}

	public Rendering getErrorRendering() {
		return errorRendering;
	}

	public void setErrorRendering(Rendering errorRendering) {
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


	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response) {
		
		ActionContext context = new ActionContext(request, response, this);
		context.setTarget(target);
		handle(context);
		
		return true;
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
	
	private String[] methods;
	
	public void setHttpMethods(String[] methods) {
		this.methods = methods;
	}
	
	public boolean supportsHttpMethod(String method) {
		if(this.methods!=null) {
			for (String m : methods) {
				if(m.equals(method)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	

	@Override
	public String[] getMethods() {
		return methods;
	}

    public void setPathVariableNames(String[] pathVariableNames) {
        this.pathVariableNames = pathVariableNames;
    }

    private String[] pathVariableNames;

    public String[] getPathVariableNames() {
        return pathVariableNames;
	}
	


}
