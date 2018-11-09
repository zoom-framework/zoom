package com.jzoom.zoom.web.action.impl;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.common.filter.impl.AnnotationFilter;
import com.jzoom.zoom.common.filter.pattern.PatternFilterFactory;
import com.jzoom.zoom.common.res.ClassResolver;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.web.action.Action;
import com.jzoom.zoom.web.action.ActionInterceptorFactory;
import com.jzoom.zoom.web.annotations.ActionFactory;
import com.jzoom.zoom.web.annotations.Controller;
import com.jzoom.zoom.web.annotations.Mapping;
import com.jzoom.zoom.web.annotations.Template;
import com.jzoom.zoom.web.router.Router;

public class SimpleActionBuilder extends ClassResolver{

	private static final Log log = LogFactory.getLog(SimpleActionBuilder.class);
	
	private IocContainer ioc;
	
	private ActionInterceptorFactory factory;

	private Router router;
	
	private Class<? extends com.jzoom.zoom.web.action.ActionFactory> defaultActionFactoryClass = SimpleActionFactory.class;
	
	private ActionFactory actionFactory;
	
	private Class<?> clazz;
	private Controller controller;
	private Object target;
	private String key;
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SimpleActionBuilder(IocContainer ioc,Router router) {
		setClassFilter(new AnnotationFilter(Controller.class));
		setClassNameFilter(PatternFilterFactory.createFilter("*.controllers.*"));

		this.ioc = ioc;
		this.router = router;
		ioc.getIocClassLoader().append(ActionInterceptorFactory.class, new SimpleActionInterceptorFactory(),true);
		factory = ioc.get(ActionInterceptorFactory.class);
	}
	

	
	
	
	@Override
	public void clear() {
		target = null;
		clazz = null;
		key = null;
		controller = null;
		actionFactory = null;
	}

	@Override
	public void visitClass(Class<?> clazz) {
		this.clazz = clazz;
		actionFactory = clazz.getAnnotation(ActionFactory.class);
		target = ioc.get(clazz);
		controller = clazz.getAnnotation(Controller.class);
		key = controller.key();
	}
	
	protected String getKey( String key, Method method,Mapping mapping) {
		if(mapping!=null) {
			if(!mapping.value().startsWith("/") && !key.endsWith("/")  && !mapping.value().isEmpty()) {
				key += "/" + mapping.value();
				
			}else {
				key += mapping.value();
			}
			
		}else {
			if(!key.endsWith("/")) {
				key += "/" + method.getName();
			}else {
				key += method.getName();
			}
		}
		if(!key.startsWith("/")) {
			key = "/" + key;
		}
		return key;
	}
	

	
	@Override
	public void visitMethod(Method method) {
		ActionFactory actionFactoryOnMethod = method.getAnnotation(ActionFactory.class);
		if(actionFactoryOnMethod == null) {
			actionFactoryOnMethod = actionFactory;
		}
		Class<? extends com.jzoom.zoom.web.action.ActionFactory> actionFactoryClass = defaultActionFactoryClass;
		if(actionFactoryOnMethod != null) {
			actionFactoryClass = actionFactoryOnMethod.value();
		}
		com.jzoom.zoom.web.action.ActionFactory factory = ioc.get(actionFactoryClass);
		Action action = factory.createAction(target, clazz, method,this.factory);
		Mapping mapping = method.getAnnotation(Mapping.class);
		String key = getKey(this.key,method,mapping);
		action.setUrl(key);
		String[] methods;
		if(mapping!=null) {
			methods = mapping.method();
		}else {
			methods = null;
		}
		action.setHttpMethods(methods);
		//模板路径
		Template template = method.getAnnotation(Template.class);
		if(template!=null) {
			action.setPath(template.path());
		}else {
			action.setPath(key.substring(1));
		}
		
		

		if(log.isInfoEnabled()) {
			log.info(String.format("注册Action成功:key:[%s] class:[%s] method:[%s] loader:[%s]", key, clazz,method.getName() ,clazz.getClassLoader()));
		}
		router.register(key,  action);
		
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
