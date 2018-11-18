package org.zoomdev.zoom.web.test.modules;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.Mockito;
import org.zoomdev.zoom.common.annotations.ApplicationModule;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.web.action.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@Module
@ApplicationModule
public class Application {


    private static  final Log log = LogFactory.getLog(Application.class);

    @Inject
    public void config(ActionInterceptorFactory factory) throws Exception {

        factory.add(getActionInterceptor(),"*",1);
    }

    private ActionInterceptor getActionInterceptor() throws Exception {

        return new ActionInterceptorAdapter(){

            @Override
            public boolean whenError(ActionContext context) throws Exception {
                log.error("发生异常"+context.getRequest().getServletPath(),context.getException());

                return super.whenError(context);
            }
        };

    }


}
