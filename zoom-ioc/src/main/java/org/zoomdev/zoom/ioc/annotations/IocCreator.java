package org.zoomdev.zoom.ioc.annotations;

import org.zoomdev.zoom.ioc.IocCreatorFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface IocCreator {

    Class<? extends IocCreatorFactory> factory();

}
