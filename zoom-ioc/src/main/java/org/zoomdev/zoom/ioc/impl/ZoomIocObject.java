package org.zoomdev.zoom.ioc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.http.Destroyable;
import org.zoomdev.zoom.http.Initializeable;
import org.zoomdev.zoom.ioc.IocClass;
import org.zoomdev.zoom.ioc.IocEvent;
import org.zoomdev.zoom.ioc.IocObject;

public class ZoomIocObject implements IocObject, Destroyable {


    private static final Log log = LogFactory.getLog(ZoomIocObject.class);

    boolean inited;

    private Object obj;

    private IocEvent destroy;

    private IocClass iocClass;

    private IocEvent init;

    public ZoomIocObject(IocClass iocClass, Object obj, boolean inited, IocEvent iocInit, IocEvent iocDestroy) {
        this.iocClass = iocClass;
        this.obj = obj;
        this.inited = inited;
        init = iocInit;
        destroy = iocDestroy;
    }

    public ZoomIocObject(IocClass iocClass, Object obj, boolean inited) {
        this(iocClass, obj, inited, null, null);
    }

    public static IocObject wrap(IocClass iocClass, Object obj, IocEvent iocInit, IocEvent iocDestroy) {
        return new ZoomIocObject(iocClass, obj, false, iocInit, iocDestroy);
    }

    public static IocObject wrap(IocClass iocClass, Object obj) {
        return new ZoomIocObject(iocClass, obj, false);
    }

    public static IocObject wrap(IocClass iocClass, Object obj, boolean inited) {
        return new ZoomIocObject(iocClass, obj, inited);
    }


    @Override
    public void destroy() {
        if (obj == null)
            return;
        if(log.isDebugEnabled()){
            log.debug("正在销毁" + obj);
        }

        try {
            if (destroy != null) {
                destroy.call(obj);
            } else {
                if (obj instanceof Destroyable) {
                    ((Destroyable) obj).destroy();
                }
            }
        } catch (Throwable t) {
            log.error("销毁对象的时候发生异常", t);
        }

        obj = null;
        iocClass = null;
    }

    @Override
    public void initialize() {
        if (init != null) {
            init.call(obj);
        } else {
            if (obj instanceof Initializeable) {
                ((Initializeable) obj).initialize();
            }
        }
    }

    @Override
    public Object get() {
        return obj;
    }

    @Override
    public void set(Object value) {
        destroy();
        this.obj = value;
    }

    @Override
    public IocClass getIocClass() {
        return iocClass;
    }


    @Override
    public String toString() {
        return obj == null ? super.toString() : obj.toString();
    }
}
