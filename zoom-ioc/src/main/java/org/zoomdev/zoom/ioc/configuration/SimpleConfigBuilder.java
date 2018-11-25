package org.zoomdev.zoom.ioc.configuration;

import org.zoomdev.zoom.common.annotations.ApplicationModule;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.filter.impl.ClassAnnotationFilter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilterFactory;
import org.zoomdev.zoom.common.res.ClassResolver;
import org.zoomdev.zoom.ioc.*;
import org.zoomdev.zoom.ioc.impl.ZoomIocKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SimpleConfigBuilder extends ClassResolver {

    protected IocContainer ioc;

    protected Class<?> clazz;

    protected List<Class<?>> list;


    public SimpleConfigBuilder(IocContainer ioc) {
        this.ioc = ioc;
        setClassNameFilter(PatternFilterFactory.createFilter("*.modules.*"));
        setClassFilter(new ClassAnnotationFilter<Class<?>>(Module.class));
        list = new ArrayList<Class<?>>();
    }


    @Override
    public void visitClass(Class<?> clazz) {
        this.clazz = clazz;
        list.add(clazz);
    }

    @Override
    public void clear() {

    }


    @Override
    public void visitMethod(Method method) {

    }

    @Override
    public boolean resolveFields() {
        return false;
    }


    @Override
    public boolean resolveMethods() {
        return false;
    }

    private Class<?> findApplication() {
        for (Class<?> type : list) {
            if (type.isAnnotationPresent(ApplicationModule.class)) {
                return type;
            }
        }
        return null;
        //throw new RuntimeException("必须有一个ZoomApplication标注的Module");
    }


    @Override
    public void endResolve() {
        //初始化application
        Class<?> app = findApplication();
        List<Class<?>> types = new ArrayList<Class<?>>();
        for (Class<?> type : list) {
            if (type.isAnnotationPresent(ApplicationModule.class)) {
                continue;
            }
            Module module = type.getAnnotation(Module.class);
            Class<? extends Annotation> annotationClass = module.value();
            if (annotationClass == Module.class
                    || (app != null && app.getAnnotation(annotationClass) != null)) {
                types.add(type);
            } else {
                log.info("没有找到对应的标注:" + annotationClass + " 模块" + type + "未启用," +
                        "若要启用本模块，请使用ApplicationModule标注主模块，并使用对应的【" + annotationClass.getName() + "】标注");
            }
        }
        if (app != null) {
            types.add(app);
        }

        for (Class<?> type : types) {
            ioc.getIocClassLoader().appendModule(type);
        }

        for(Class<?> type : types){
            IocObject module = ioc.get(new ZoomIocKey(type));
            IocClass iocClass = module.getIocClass();
            IocMethod[] methods = iocClass.getIocMethods();
            if(methods==null || methods.length ==0){
                continue;
            }
            int index = 0;
            for(IocMethod method : methods){
                if(method==null){
                    continue;
                }
                if(hasSystemOrder(method)){
                    //inject
                    method.inject(module);
                    methods[index] = null;
                }
                ++index;
            }
        }

        for(Class<?> type : types){
            ioc.fetch(type);
        }


        list.clear();

    }

    private boolean hasSystemOrder(IocMethod method){
        IocKey[] keys = method.getParameterKeys();
        for(IocKey key : keys){
            IocClass iocClass = ioc.getIocClassLoader().get(key);
            if(iocClass==null){
                throw new IocException("未找到指定的IocClass:"+key);
            }
            if(iocClass.getOrder() == IocBean.SYSTEM){
                return true;
            }
        }
        return false;
    }
}
