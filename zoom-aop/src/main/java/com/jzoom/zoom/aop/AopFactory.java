package com.jzoom.zoom.aop;


import com.jzoom.zoom.common.filter.ClassAndMethodFilter;
import com.jzoom.zoom.ioc.ClassEnhancer;

/**
 * 增强工厂
 * 
 * 思路：
 * 一、改造增强
 * 
 * 对于一个需要aop的类，需要生成一个对应的子类来增强功能，比如：
 * class MyModel{
 * 	public int add(int a,int b){
 * 		return a+b;	
 *  }
 * }
 * 
 * 现在我想增强add这个方法的功能，将add方法执行过程中的参数、返回值等等信息输出来，那么可以这样来做
 * 
 * 生成一个新类
 * class MyModel$Enhance{
 * 	
 * 	public int add(int a,int b){
 *  		int c = super.add(a+b);
 *  		log.info("参数:[a,b] 返回值[c]");
 *  		return c;
 *  }
 * }
 * 
 * 这样的实现有一个非常大的问题：无法控制到底要不要调用super.add。
 * 在aop中需要灵活的去控制是否调用原方法的实现， 修改执行参数，修改返回值，修改逻辑。
 * 那么可以封装一层接口；  @see MethodInterceptor
 *
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * @author jzoom
 *
 */
public interface AopFactory extends ClassEnhancer {

	/**
	 * Generate a new class by enhance the original class.
	 * 增强类的功能，产生一个新的类
	 * @param targetClass			目标类
	 * @return
	 */
	Class<?> enhance(Class<?> targetClass);

	/**
	 * 为模式pattern增加方法拦截器
	 * @param interceptor
	 * @param pattern {@link com.jzoom.zoom.common.filter.ClassAndMethodFilter}
     *                {@link com.jzoom.zoom.common.filter.pattern.PatternFilter}
     *                {@link com.jzoom.zoom.common.filter.pattern.PatternFilterFactory}
	 * @return
	 */
	AopFactory addFilter(MethodInterceptor interceptor,String pattern,int order);

	/**
	 * 为模式ClassAndMethodFilter增加方法拦截器
	 * @param interceptor
	 * @param filter  {@link com.jzoom.zoom.common.filter.ClassAndMethodFilter}
	 * @param order
	 * @return
	 */
	AopFactory addFilter(MethodInterceptor interceptor, ClassAndMethodFilter filter, int order);
	
	/**
	 * 按照顺序增加{@link MethodInterceptorFactory}
	 * @param maker
	 * @param order
	 * @return
	 */
	AopFactory methodInterceptorFactory(MethodInterceptorFactory maker, int order);
	
}
