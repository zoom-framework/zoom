package org.zoomdev.zoom.web.utils;


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
                for(final ClassResolver resolver : resolvers){
                    resolver.resolve(scanner);
                }

                WebUtils.setStartupSuccess();
            }
        });
        thread.setDaemon(true);
        thread.setName("Startup Thread");
        thread.start();

    }


}
