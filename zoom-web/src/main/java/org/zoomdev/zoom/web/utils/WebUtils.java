package org.zoomdev.zoom.web.utils;

import org.zoomdev.zoom.async.impl.Asyncs;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.ioc.IocContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebUtils {

    private static IocContainer ioc;

    public static void setIoc(IocContainer ioc) {
        WebUtils.ioc = ioc;
    }

    public static IocContainer getIoc() {
        return ioc;
    }


    private static AtomicBoolean startupSuccess = new AtomicBoolean(false);

    private static Collection<Runnable> queue = Collections.synchronizedList(new ArrayList<Runnable>());
    private static Collection<Runnable> after = new ArrayList<Runnable>();

    /**
     * 最后处理
     */
    public static void runAfter(Runnable runnable){
        if(startupSuccess.get()){
            throw new ZoomException("Already startup");
        }
        after.add(runnable);
    }


    /**
     * 在web启动之后异步运行,这个用于启动过程中，有某些步骤是需要在启动完成之后才能做，并且需要脱离启动线程
     */
    public static void runAfterAsync(Runnable runnable) {
        synchronized (queue) {
            if (startupSuccess.get()) {
                //just do
                Asyncs.defaultJobQueue().run(runnable);
            } else {
                queue.add(runnable);
            }
        }

    }

    public static void setStartupSuccess() {

        for(Runnable runnable : after){
            runnable.run();
        }

        synchronized (queue) {
            startupSuccess.set(true);
            for (Runnable runnable : queue) {
                Asyncs.defaultJobQueue().run(runnable);
            }
        }

    }
}
