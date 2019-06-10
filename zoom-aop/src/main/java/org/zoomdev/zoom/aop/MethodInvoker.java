package org.zoomdev.zoom.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.aop.impl.AstractMethodInterceptorFactory;
import org.zoomdev.zoom.common.utils.Classes;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;

/**
 * 方法执行aop容器
 *
 * @author jzoom
 */
public class MethodInvoker {

    private Object target;
    private Object[] args;
    private Method method;
    private Object returnObject;
    private int index;
    private MethodInterceptor[] interceptors;
    private MethodCaller caller;
    private boolean invoked;


    public MethodInvoker(AstractMethodInterceptorFactory.AopConfig config, Object target, Object[] args) {
        this.target = target;
        this.method = config.getMethod();
        this.args = args;
        this.caller = config.getCaller();
        this.interceptors = config.getInterceptors();
    }

    /**
     * 获取调用对象
     *
     * @return
     */
    public Object getTarget() {
        return target;
    }

    /**
     * 替换参数
     *
     * @param index
     * @param value
     */
    public void setArg(int index, Object value) {

        if (index < 0 || index >= args.length) {
            throw new InvalidParameterException("index必须>=0&&<args.length");
        }

        args[index] = value;
    }

    /**
     * 替换全部参数，注意长度必须一致
     */
    public void setArgs(Object[] args) {

        assert (args != null);

        if (args.length != this.args.length) {
            throw new InvalidParameterException("参数长度必须一致");
        }

        this.args = args;
    }


    /**
     * 获取调用参数
     *
     * @return
     */
    public Object[] getArgs() {
        return args;
    }


    /**
     * 获取方法
     *
     * @return
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 执行
     */
    public void invoke() throws Throwable {
        if (index < interceptors.length && !invoked) {
            interceptors[index++].intercept(this);
        } else {
            if (!invoked) {
                //调用原来的逻辑
                if (index++ == interceptors.length) {
                    try {
                        returnObject = caller.invoke(target, args);
                    } catch (Throwable e) {
                        throw Classes.getCause(e);
                    } finally {
                        invoked = true;
                    }
                }
            }

            //如果已经执行了，那么就直接返回
        }
    }

    /**
     * 获取返回值
     *
     * @return
     */
    public Object getReturnObject() {
//		if(!invoked) {
//			throw new ZoomException("必须在调用invoke之后才可以使用这个方法，否则无意义");
//		}
        return returnObject;
    }

    public void setReturnObject(Object value, boolean setInvoked) {
        returnObject = value;
        if (setInvoked) {
            invoked = true;
        }

    }

    public void setReturnObject(Object value) {
        setReturnObject(value, true);
    }

    public boolean isInvoked() {
        return invoked;
    }


    @SuppressWarnings("unchecked")
    public <T> T getArg(int index) {
        assert (index >= 0 && index < this.args.length);
        return (T) args[index];
    }
}
