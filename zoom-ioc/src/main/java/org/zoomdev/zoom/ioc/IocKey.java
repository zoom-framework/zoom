package org.zoomdev.zoom.ioc;

/**
 * 表示一个ioc对象的标志
 * 任何一个ioc对象都要有三个要素:
 * 
 * 1、如何创建这个对象
 * 2、这个对象的类型是什么
 * 	(在ioc容器中，每一个对象的类型只有一种，不管实现了多少接口,要么是这个对象的实际类型，要么是这个对象的其中一个接口）
 * 3、这个对象在ioc容器中的名称  
 * 	对于匿名对象，ioc容器中的类型一定为这个对象的实际类型。
 *
 * @author jzoom
 *
 */
public interface IocKey {

	/**
	 * 在ioc容器中的名称，可能为null
	 * @return
	 */
	String getName();
	
	/**
	 * 在ioc容器中的类型，比如有，可能为interface,也可能为实际class
	 * @return
	 */
	Class<?> getType();
	

	
	/**
     * 重写这个方法
	 * @return
	 */
	int hashCode();

    /**
     * 是否有名称
     *
     * @return
     */
    boolean hasName();

    /*
     * 是否是接口
     */
    boolean isInterface();
	
}
