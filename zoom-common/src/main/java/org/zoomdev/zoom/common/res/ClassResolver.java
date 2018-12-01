package org.zoomdev.zoom.common.res;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.Clearable;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.filter.Filter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 对类进行解析,本类选择性的对一些类进行解析，可以指定类名、类进行解析，可以指定是否解析方法和字段
 * 非线程安全，需要注意线程同步
 *
 * @author jzoom
 */
public abstract class ClassResolver{

    protected static Log log = LogFactory.getLog(ClassResolver.class);



    public ClassResolver() {
    }



    public abstract void resolve(ResScanner scanner);

}
