package org.zoomdev.zoom.web.utils;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.async.impl.Asyncs;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.res.ClassResolver;
import org.zoomdev.zoom.common.res.ResScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

;

public class ClassResolvers  {

    List<ClassResolver> resolvers;

    private static final Log log = LogFactory.getLog(ClassResolvers.class);


    public ClassResolvers(ClassResolver... resolvers) {
        this.resolvers = Arrays.asList(resolvers);
    }

    public void add(ClassResolver classResolver) {
        this.resolvers.add(classResolver);
    }

    public void visit(final ResScanner scanner) {



        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
               try{
                   for(final ClassResolver resolver : resolvers){
                       resolver.resolve(scanner);
                   }

                   WebUtils.setStartupSuccess();

                   log.info("============ClassResolvers解析成功============");
               }catch (Throwable t){
                   log.error("启动发生异常",t);
               }
            }
        });
        thread.setDaemon(true);
        thread.setName("Startup Thread");
        thread.start();

    }


}
