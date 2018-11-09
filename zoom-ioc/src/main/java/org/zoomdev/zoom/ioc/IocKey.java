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
	 * 实际类型的ClassLoader,
	 * 比如a.jar 中实现了 A 这个接口， b.jar中的类 C 实现了 A 这个接口，
	 * a.jar 由 AClassLoader加载， b.jar由BClassLoader加载， 在ioc容器中的实际类为 C ,那么这里的结果为 BClassLoader,
     * 而上述getType可能为 A ，也可能为 C,
     * 也就是说，同一个接口对象，在ioc容器中可能存在不同的匿名对象。
     *
     * 针对本情况，也可以这么实现：
     * Key必须和Ioc容器本身的ClassLoader一致，才能取出值。
     *
     * 不同的classLoader加载不同的Ioc容器，可以使用其他的ioc容器作为父容器，本classLoader的优先。
     *
	 * @return
	 */
	ClassLoader getClassLoader();
	
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
