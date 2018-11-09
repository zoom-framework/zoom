package org.zoomdev.zoom.ioc.modules;

import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.IocEventListener;
import org.zoomdev.zoom.ioc.IocMethodVisitor;
import org.zoomdev.zoom.ioc.impl.IocMethodVisitorImpl;

@Module
public class IocModule {

    @IocBean
    public IocMethodVisitor getIocMethodVisitor(){
        return new IocMethodVisitorImpl();
    }

    @Inject
    public void inject(IocContainer ioc, IocMethodVisitor visitor) {
        ioc.addEventListener((IocEventListener)visitor);
    }
}
