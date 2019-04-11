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
        final List<Future> list = new ArrayList<Future>(resolvers.size());
        for(final ClassResolver resolver : resolvers){
            Future future = Asyncs.defaultJobQueue().submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    resolver.resolve(scanner);
                    return null;
                }

            });
            list.add(future);
        }


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(Future future : list){
                    try {
                        future.get();
                    } catch (InterruptedException e) {
                        return;
                    } catch (ExecutionException e) {
                        throw new ZoomException(e);
                    }
                }

                WebUtils.setStartupSuccess();
            }
        });
        thread.setDaemon(true);
        thread.setName("Startup Thread");
        thread.start();

    }


}
