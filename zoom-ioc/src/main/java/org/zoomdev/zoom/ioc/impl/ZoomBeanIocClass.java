package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.*;

/**
 * 一开始，并不知道所有的Constructor,必须要等实例化之后才知道
 *
 * @author jzoom
 */
public class ZoomBeanIocClass extends ZoomIocClass {

    private IocContainer iocContainer;

    public ZoomBeanIocClass(
            IocContainer ioc,
            IocClassLoader classLoader,
            IocConstructor constructor,
            IocKey key) {
        super(ioc, classLoader, constructor, key);
    }

    private boolean injectorCreated = false;


    private void createInjector(IocContainer ioc, IocScope scope, Object instance) {
        injectorCreated = true;
        methods = ZoomIocContainer.parseMethods(ioc, this, instance.getClass(), classLoader);
        fields = ZoomIocContainer.parseFields(ioc, instance.getClass(), classLoader);

//        if(fields!=null) {
//            for (IocField field : fields) {
//                try{
//                    IocClass iocClass = classLoader.get(field.getKey());
//                    if(iocClass!=null){
//                        iocClass.newInstance(scope);
//                    }else if( field.getValue() == IocValues.VALUE ){
//                        throw new IocException("初始化ioc field失败"+field.getField()+" 未取到能设置的IocClass");
//                    }
//
//                }catch (Throwable e){
//                    throw new IocException("初始化ioc field失败"+field.getField(),e);
//                }
//
//            }
//        }
//
//        if(methods!=null) {
//            for (IocMethod method : methods) {
//                for (IocKey key : method.getParameterKeys()) {
//                    try{
//                        IocClass iocClass = classLoader.get(key);
//                        if(iocClass==null){
//                            throw new IocException("初始化ioc method失败,获取key失败"+key);
//                        }
//                        iocClass.newInstance(scope);
//                    }catch (Throwable e){
//                        throw new IocException("初始化ioc method失败"+method.getMethod(),e);
//                    }
//
//                }
//            }
//        }
    }


    @Override
    public IocObject newInstance(IocScope scope) {
        IocObject obj = getAndCreate(scope, constructor);
        Object instance = obj.get();

        if (!injectorCreated) {
            createInjector(ioc, scope, instance);
        }


        return obj;
    }

}
