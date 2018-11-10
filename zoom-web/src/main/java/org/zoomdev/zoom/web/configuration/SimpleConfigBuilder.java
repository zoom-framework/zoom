package org.zoomdev.zoom.web.configuration;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.filter.impl.ClassAnnotationFilter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilterFactory;
import org.zoomdev.zoom.common.res.ClassResolver;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.IocException;
import org.zoomdev.zoom.web.annotations.ZoomApplication;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SimpleConfigBuilder extends ClassResolver {

	private IocContainer ioc;

	private Class<?> clazz;
	
	private List<Class<?>> list;

	
	public SimpleConfigBuilder(IocContainer ioc) {
		this.ioc= ioc;
		setClassNameFilter(PatternFilterFactory.createFilter("*.modules.*"));
		setClassFilter( new ClassAnnotationFilter<Class<?>>( Module.class )  );
		list = new ArrayList<Class<?>>();
	}

	
	@Override
	public void visitClass(Class<?> clazz) {
		this.clazz = clazz;
		list.add(clazz);
	}
	
	@Override
	public void clear() {
		
	}

	
	@Override
	public void visitMethod(Method method) {
		IocBean bean = method.getAnnotation(IocBean.class);
		if(bean != null) {
			//classFactory.registerIocBean(bean,clazz, method);
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
	
	private Class<?> findApplication(){
		for (Class<?> type : list) {
			if(type.isAnnotationPresent(ZoomApplication.class)) {
				return type;
			}
		}
		return null;
		//throw new RuntimeException("必须有一个ZoomApplication标注的Module");
	}

	
	@Override
	public void endResolve() {
		//初始化application
		
		Class<?> app = findApplication();
		List<Class<?>> types = new ArrayList<Class<?>>();
		for (Class<?> type : list) {
			if(type.isAnnotationPresent(ZoomApplication.class)) {
				continue;
			}
			Module module = type.getAnnotation(Module.class);
			Class<? extends Annotation> annotationClass = module.value();
			if(annotationClass==Module.class || (app!=null && app.getAnnotation(annotationClass)!=null) ) {
				log.info(String.format( "初始化Module [%s]" ,type));
				types.add(type);
			}else {
				log.info("没有找到对应的标注:" + annotationClass + " 模块"+ type +"未启用");
			}
		}
		if(app!=null) {
			types.add(app);
		}

		for(Class<?> type : types){
			try {
				Object module = Classes.newInstance(type);
				ioc.getIocClassLoader().append(type,module);
				//bean
				Method[] methods = CachedClasses.getPublicMethods(type);
				for (Method method : methods) {
					IocBean bean = method.getAnnotation(IocBean.class);
					if(bean != null) {
						ioc.getIocClassLoader().append(module,method);
					}
				}
			} catch (Exception e) {
				throw new IocException("Module初始化失败，Module必须有一个默认构造函数",e);
			}
		}

		for(Class<?> type : types){
			ioc.get(type);
		}

		list.clear();

	}
}