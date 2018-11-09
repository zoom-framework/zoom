package com.jzoom.zoom.ioc.modules;

import com.jzoom.zoom.common.annotations.Inject;
import com.jzoom.zoom.common.annotations.IocBean;
import com.jzoom.zoom.common.annotations.Module;
import com.jzoom.zoom.ioc.IocContainer;
import com.jzoom.zoom.ioc.IocEventListener;
import com.jzoom.zoom.ioc.IocMethodVisitor;
import com.jzoom.zoom.ioc.impl.IocMethodVisitorImpl;

@Module
public class IocModule {

    @IocBean
    public IocMethodVisitor getIocMethodVisitor(){
        return new IocMethodVisitorImpl();
    }

    @Inject
    public void inject(IocContainer ioc,IocMethodVisitor visitor) {
        ioc.addEventListener((IocEventListener)visitor);
    }
}
