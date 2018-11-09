package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.common.Initializeable;
import com.jzoom.zoom.ioc.IocClass;
import com.jzoom.zoom.ioc.IocEvent;
import com.jzoom.zoom.ioc.IocObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ZoomIocObject implements IocObject,Destroyable {


	private static final Log log = LogFactory.getLog(ZoomIocObject.class);
	
	private boolean inited;
	
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
    public boolean isInitialized() {
		return inited;
	}

	@Override
	public void destroy() {
		if(obj==null)
			return;
		log.info("正在销毁"+obj);
		try{
            if(destroy!=null) {
                destroy.call(obj);
            }else {
                if(obj instanceof Destroyable) {
                    ((Destroyable)obj).destroy();
                }
            }
        }catch (Throwable t){
            log.error("销毁对象的时候发生异常",t);
        }

		obj = null;
        iocClass = null;
	}

	@Override
    public void initialize() {
		inited = true;
		if(init != null){
		    init.call(obj);
        }else{
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
