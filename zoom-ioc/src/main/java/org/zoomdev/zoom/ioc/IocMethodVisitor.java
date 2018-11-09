package org.zoomdev.zoom.ioc;

/**
 * 对于ioc创建出来之后，需要对method进行遍历，如果符合条件，那么可以保存注册的ioc对象和method的引用
 */
public interface IocMethodVisitor {
    void add(IocMethodHandler handler);
}
