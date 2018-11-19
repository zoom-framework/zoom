package org.zoomdev.zoom.web.test.modules;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.annotations.ApplicationModule;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.web.action.*;

@Module
@ApplicationModule
public class Application {


    private static final Log log = LogFactory.getLog(Application.class);

    @Inject
    public void config(ActionInterceptorFactory factory, ActionInterceptor interceptor) throws Exception {

        factory.add(interceptor, "*", 1);
    }

    @IocBean
    public ActionInterceptor getActionInterceptor(final Monitor monitor) throws Exception {


        return new ActionInterceptorAdapter() {

            @Override
            public boolean preParse(ActionContext context) throws Exception {

                return super.preParse(context);
            }

            @Override
            public void parse(ActionContext context) throws Exception {
                super.parse(context);

            }

            @Override
            public void whenResult(ActionContext context) throws Exception {
                super.whenResult(context);
                monitor.setArguments(context.getArgs());
            }

            @Override
            public boolean whenError(ActionContext context) throws Exception {
                log.error("发生异常" + context.getRequest().getServletPath(), context.getException());

                return super.whenError(context);
            }
        };

    }


}
