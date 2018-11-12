package org.zoomdev.zoom.ioc.configuration;

import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.annotations.ZoomApplication;
import org.zoomdev.zoom.common.filter.impl.ClassAnnotationFilter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilterFactory;
import org.zoomdev.zoom.common.res.ClassResolver;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.IocException;

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

	}

	@Override
	public boolean resolveFields() {
		return false;
	}


	@Override
	public boolean resolveMethods() {
		return false;
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
			if(annotationClass==Module.class
                    || (app!=null && app.getAnnotation(annotationClass)!=null)
                    || app == null) {

				types.add(type);
			}else {
				log.info("没有找到对应的标注:" + annotationClass + " 模块"+ type +"未启用");
			}
		}
		if(app!=null) {
			types.add(app);
		}

		for(Class<?> type : types){
			ioc.getIocClassLoader().appendModule(type);
		}

		for(Class<?> type : types){
			ioc.get(type);
		}

		list.clear();

	}
}
