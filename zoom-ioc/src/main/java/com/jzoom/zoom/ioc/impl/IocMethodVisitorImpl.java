package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.common.utils.CachedClasses;
import com.jzoom.zoom.ioc.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class IocMethodVisitorImpl implements IocEventListener,IocMethodVisitor {

    private List<IocMethodHandler> handlers;

    public IocMethodVisitorImpl() {
        handlers = new ArrayList<IocMethodHandler>();
    }

    public void add(IocMethodHandler handler){
        this.handlers.add(handler);
    }



    @Override
    public void onObjectCreated(IocScope scope, IocObject object) {
        Object data = object.get();
        assert(data!=null);
        IocContainer ioc = scope.getIoc();
        Method[] methods = CachedClasses.getPublicMethods(data.getClass());
        for(Method method : methods){
            for(IocMethodHandler handler : handlers){
                if(handler.accept(method)){
                    handler.visit(object, method, ioc.getMethodProxy(object.getIocClass(), method));
                }
            }
        }
    }
}
