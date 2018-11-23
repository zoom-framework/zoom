package org.zoomdev.zoom.web.test.modules;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.annotations.ApplicationModule;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.web.WebConfig;
import org.zoomdev.zoom.web.action.*;
import org.zoomdev.zoom.web.rendering.TemplateEngineManager;
import org.zoomdev.zoom.web.rendering.impl.TemplateRendering;
import org.zoomdev.zoom.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

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
                monitor.setArguments(context.getResponse(),context.getArgs());
            }

            @Override
            public boolean whenError(ActionContext context) throws Exception {
                log.error("发生异常" + context.getRequest().getServletPath(), context.getException());
                monitor.setArguments(context.getResponse());
                return super.whenError(context);
            }
        };

    }


    public static class TestRendering extends TemplateRendering{



        public TestRendering(WebConfig webConfig) {
            super(webConfig);
        }

        @Override
        protected void render(HttpServletRequest request, HttpServletResponse response, String path, Map<String, Object> data) throws Exception {
           if(response==null){
               throw new ZoomException("response is null");
           }

           if(response.getWriter()==null)
           {
               throw new ZoomException("writer is null");
           }
            response.getWriter().print(path);
        }

        @Override
        public boolean shouldHandle(Class<?> targetClass, Method method) {
            return true;
        }

        @Override
        public String getUid() {
            return "testRendering";
        }
    }


    @Inject
    public void config(TemplateEngineManager manager,
                       WebConfig webConfig){

        manager.register("html",new TestRendering(webConfig) );


        WebUtils.runAfterAsync(new Runnable() {
            @Override
            public void run() {

                System.out.println("This is run after startup");

            }
        });


        WebUtils.runAfterAsync(new Runnable() {
            @Override
            public void run() {

                System.out.println("This is run after startup2");

            }
        });
    }


}
