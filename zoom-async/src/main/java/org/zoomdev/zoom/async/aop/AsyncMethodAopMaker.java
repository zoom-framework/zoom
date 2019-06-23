package org.zoomdev.zoom.async.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.MethodInvoker;
import org.zoomdev.zoom.aop.factory.AnnotationMethodInterceptorFactory;
import org.zoomdev.zoom.async.annotation.Async;
import org.zoomdev.zoom.async.impl.Asyncs;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.utils.Classes;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class AsyncMethodAopMaker extends AnnotationMethodInterceptorFactory<Async> {

    private static final Log log = LogFactory.getLog(AsyncMethodAopMaker.class);

    @Override
    protected void createMethodInterceptors(Async annotation, Method method, List<MethodInterceptor> interceptors) {

        if (Future.class.isAssignableFrom(method.getReturnType())) {
            interceptors.add(interceptorWithFuture);
        } else {
            interceptors.add(interceptor);
        }
    }

    MethodInterceptor interceptorWithFuture = new AsyncMethodInterceptorWithFuture();
    MethodInterceptor interceptor = new AsyncMethodInterceptor();

    private static class AsyncMethodInterceptorWithFuture implements MethodInterceptor {


        private AsyncMethodInterceptorWithFuture() {

        }

        @Override
        public void intercept(final MethodInvoker invoker) throws Throwable {
            Future future = Asyncs.defaultJobQueue().submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    try {
                        invoker.invoke();
                        Object value = invoker.getReturnObject();
                        /// 仍然是一个future
                        if (value instanceof Future) {
                            value = ((Future) value).get();
                        }
                        return value;
                    } catch (Throwable e) {
                        log.error("执行异步出错",e);
                        throw new ZoomException("执行错误", e);
                    }

                }
            });
            invoker.setReturnObject(future, false);
        }

    }

    private static class AsyncMethodInterceptor implements MethodInterceptor {


        private AsyncMethodInterceptor() {

        }

        @Override
        public void intercept(final MethodInvoker invoker) throws Throwable {
            Asyncs.defaultJobQueue().submit(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    try {
                        invoker.invoke();
                        return invoker.getReturnObject();
                    } catch (Throwable e) {
                        //如果这里出错了怎么办?
                        log.error("执行异步出错",e);
                        throw new ZoomException("执行错误", e);
                    }
                }
            });

        }

    }


}
