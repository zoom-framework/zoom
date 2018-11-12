package org.zoomdev.zoom.dao.factory;

import java.lang.annotation.Annotation;

public interface DaoParameterFactory<T extends Annotation> {


    DaoParameter create(
            Class<?> type,
            T annotation
    );

    boolean isAnnotation(Annotation annotation);

}
