package org.zoomdev.zoom.web.utils;


import org.zoomdev.zoom.async.impl.Asyncs;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.res.ClassResolver;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.res.ResScanner.ClassRes;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.Visitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

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

        Asyncs.defaultJobQueue().run(new Runnable() {
            @Override
            public void run() {
                for(Future future : list){
                    try {
                        future.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        throw new ZoomException(e);
                    }
                }

                WebUtils.setStartupSuccess();
            }
        });

    }


}
