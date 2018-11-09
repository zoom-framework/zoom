package com.jzoom.zoom.common.filter.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.common.filter.Filter;

public class AnnotationFilter<T extends AnnotatedElement> implements Filter< T >,Destroyable {
	
	private Class<? extends Annotation> annotationClass;
	
	public AnnotationFilter(Class<? extends Annotation> annotationClass) {
		assert(annotationClass!=null);
		this.annotationClass = annotationClass;
	}

	@Override
	public boolean accept(T value) {
		assert(value!=null);
		return value.isAnnotationPresent(annotationClass);
	}

	@Override
	public void destroy() {
		this.annotationClass = null;
	}

}
