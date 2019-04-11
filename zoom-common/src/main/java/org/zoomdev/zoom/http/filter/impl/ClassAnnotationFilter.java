package org.zoomdev.zoom.http.filter.impl;

import org.zoomdev.zoom.http.Destroyable;
import org.zoomdev.zoom.http.filter.Filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class ClassAnnotationFilter<T extends AnnotatedElement> implements Filter<T>, Destroyable {

    private Class<? extends Annotation> annotationClass;

    public ClassAnnotationFilter(Class<? extends Annotation> annotationClass) {
        assert (annotationClass != null);
        this.annotationClass = annotationClass;
    }

    @Override
    public boolean accept(T value) {
        assert (value != null);
        return value.isAnnotationPresent(annotationClass);
    }

    @Override
    public void destroy() {
        this.annotationClass = null;
    }

}
