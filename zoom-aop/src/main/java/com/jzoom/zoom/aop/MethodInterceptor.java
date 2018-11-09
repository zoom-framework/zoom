package com.jzoom.zoom.aop;

/**
 * 
 * 
 * 这个接口实现方法的切面，在这个接口中，最终调用方法的是
 * @see MethodInvoker#invoke()
 * 
 * 比如实现这么个切面:
 * 
 * class TestMethodInterceptor implements MethodInterceptor{
 * 	void intercept( MethodInvoker invoker) throws Throwable{
 *  		//这里你可以决定使用原来的逻辑
 *  		invoker.invoke();
 *  
 *  		//也可以不用原来的逻辑:
 *      invoker.setReturnObject(  我的返回值  )
 *      
 *      //可以替换参数:
 *      invoker.setArg(0, 我的参数)
 *      
 *  }
 * }
 * 
 * 
 * 
 * @author jzoom
 *
 */
public interface MethodInterceptor {

	/**
	 * 对方法进行切面
	 * @param invoker
	 */
	void intercept( MethodInvoker invoker) throws Throwable;
	
}
