package org.zoomdev.zoom.ioc;

/**
 * ioc object
 * @author jzoom
 *
 */
public interface IocObject {

    /**
     * 是否已经初始化
     *
     * @return
     */
    boolean isInitialized();

    /**
     * 进行初始化
     */
    void initialize();


    /**
     * 获取到对象
     *
     * @return
     */
    Object get();

	/**
	 * 用于运行时动态设置
	 * @param value
	 */
	void set(Object value);

    /**
     * 类似Object.getClass
     *
     * @return
     */
    IocClass getIocClass();


    /**
     * 销毁ioc对象
     */
    void destroy();

}
