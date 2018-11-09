package org.zoomdev.zoom.common.filter.impl;

import java.lang.reflect.Method;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.filter.ClassAndMethodFilter;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilterFactory;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.filter.ClassAndMethodFilter;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilterFactory;

/**
 * 创建一个类、方法过滤器
 * @see PatternFilterFactory
 * @see PatternFilter
 * @author jzoom
 *
 */
public class PatternClassAndMethodFilter implements ClassAndMethodFilter,Destroyable {
	
	private Filter<String> classNameFilter;
	private Filter<String> methodNameFilter;
	
	/**
	 * 形式为  com.aaa.MyClass#method0
	 * 如:   *.models.*#*   对应所有models下的所有类的所有方法    
	 * 
	 * @param pattern
	 */
	public PatternClassAndMethodFilter(String pattern) {
		if(!pattern.contains("#")) {
			//默认方法*
			methodNameFilter = PatternFilterFactory.createFilter("*");
			classNameFilter = PatternFilterFactory.createFilter(pattern);
		}else {
			String[] parts = pattern.split("#");
			methodNameFilter = PatternFilterFactory.createFilter(parts[1]);
			classNameFilter = PatternFilterFactory.createFilter(parts[0]);
		}
	}
	@Override
	public void destroy() {
		methodNameFilter = null;
		classNameFilter = null;
	}
	
	public boolean accept(String className) {
		assert(className!=null);
		return classNameFilter.accept(className);
	}
	
	
	public boolean accept(Class<?> clazz) {
		assert(clazz!=null);
		return accept(clazz.getName());
	}
	
	public boolean accept(Class<?> clazz,Method method) {
		return methodNameFilter.accept(method.getName());
	}

	
	
}
