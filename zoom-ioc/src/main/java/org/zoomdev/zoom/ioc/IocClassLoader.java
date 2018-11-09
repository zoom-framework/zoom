package org.zoomdev.zoom.ioc;

import java.lang.reflect.Method;

public interface IocClassLoader {

	IocClass get(IocKey key);

	/**
	 * 增加一个IocBean方法配置
     * @param moduleInstance                module实例
     * @param method                        module的方法
	 */
    IocClass append(Object moduleInstance, Method method);

	/**
     * 注册增加一个实例
	 * @param baseType
	 * @param instance
     * @param initialized
     * @return
	 */
    IocClass append(Class<?> baseType, Object instance, boolean initialized);

    /**
     * 注册增加一个实例,这个方法可以为ioc容器直接注册一个对象
     *
     * @param baseType
     * @param instance
     * @return
     */
	IocClass append(Class<?> baseType,Object instance);
	/**
	 * 直接增加一个实际类
	 * @param type
	 */
	IocClass append(Class<?> type);


	void setClassEnhancer(ClassEnhancer enhancer);
	
}
