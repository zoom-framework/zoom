package org.zoomdev.zoom.ioc.modules;

import org.zoomdev.zoom.http.annotations.Inject;
import org.zoomdev.zoom.http.annotations.IocBean;
import org.zoomdev.zoom.http.annotations.Module;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.IocEventListener;
import org.zoomdev.zoom.ioc.IocMethodVisitor;

@Module
public class IocModule {

    @IocBean
    public IocMethodVisitor getIocMethodVisitor() {
        return new IocMethodVisitorImpl();
    }

    @Inject
    public void inject(IocContainer ioc, IocMethodVisitor visitor) {
        ioc.addEventListener((IocEventListener) visitor);
    }
}
