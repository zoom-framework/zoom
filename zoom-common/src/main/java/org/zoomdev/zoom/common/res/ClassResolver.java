package org.zoomdev.zoom.common.res;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.zoomdev.zoom.common.Clearable;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.filter.Filter;

/**
 * 对类进行解析,本类选择性的对一些类进行解析，可以指定类名、类进行解析，可以指定是否解析方法和字段
 * @author jzoom
 *
 */
public abstract class ClassResolver implements Clearable,Destroyable {
	
	protected static Log log = LogFactory.getLog(ClassResolver.class);
	
	protected Filter<String> patternFilter;
	protected Filter<Class<?>> classFilter;
	
	public ClassResolver() {
	}
	
	
	public void destroy() {
		clear();
		this.patternFilter = null;
		this.classFilter = null;
	}
	

	/**
	 * 对名称是否有要求?
	 */
	public boolean acceptClassName(String className) {
		return patternFilter == null ? true : patternFilter.accept(className);
	}

	/**
	 * 对类是否有要求?
	 * @param clazz
	 * @return
	 */
	public boolean acceptClass(Class<?> clazz) {
		return classFilter == null ? true : classFilter.accept(clazz);
	}
	
	
	public void visitMethod(Method method) {
		
	}
	
	
	public void visitField(Field field) {
		
	}

	public void visitClass(Class<?> clazz) {
		
	}

	public Filter<String> getPatternFilter() {
		return patternFilter;
	}

	public void setClassNameFilter(Filter<String> patternFilter) {
		this.patternFilter = patternFilter;
	}

	public Filter<Class<?>> getClassFilter() {
		return classFilter;
	}

	public void setClassFilter(Filter<Class<?>> classFilter) {
		this.classFilter = classFilter;
	}


	/**
	 * 清理，当进行完一个类的解析之后，本方法会被调用
	 */
	@Override
	public void clear() {
		
	}


	/**
	 * 是否解析字段?
	 * @return
	 */
	public abstract boolean resolveFields() ;


	/**
	 * 是否解析方法?
	 * @return
	 */
	public abstract boolean resolveMethods() ;

	
	/**
	 * 结束
	 */
	public void endResolve() {
		
	}
	
	

}
