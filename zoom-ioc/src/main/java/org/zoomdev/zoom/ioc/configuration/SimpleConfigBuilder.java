package org.zoomdev.zoom.ioc.configuration;

import org.zoomdev.zoom.common.annotations.ApplicationModule;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.res.ClassResolver;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.OrderedList;
import org.zoomdev.zoom.ioc.*;
import org.zoomdev.zoom.ioc.impl.ZoomIocKey;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class SimpleConfigBuilder extends ClassResolver {

    protected IocContainer ioc;

    protected Class<?> clazz;

    protected List<Class<?>> list;


    public SimpleConfigBuilder(IocContainer ioc) {
        this.ioc = ioc;
        list = new ArrayList<Class<?>>();
    }

    @Override
    public void resolve(ResScanner scanner) {
        List<ResScanner.ClassRes> classes= scanner.findClass("*.modules.*");
        for(ResScanner.ClassRes res : classes){
            Class<?> type = res.getType();
            Module module = type.getAnnotation(Module.class);
            if(module==null)continue;
            list.add(type);
        }

        endResolve();
    }



    private Class<?> findApplication() {
        for (Class<?> type : list) {
            if (type.isAnnotationPresent(ApplicationModule.class)) {
                return type;
            }
        }
        return null;
        //throw new ZoomException("必须有一个ZoomApplication标注的Module");
    }

    public void endResolve() {
        //初始化application
        Class<?> app = findApplication();
        final List<Class<?>> types = new ArrayList<Class<?>>();
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
                if(log.isDebugEnabled())
                    log.debug("没有找到对应的标注:" + annotationClass + " 模块" + type + "未启用," +
                        "若要启用本模块，请使用ApplicationModule标注主模块，并使用对应的【" + annotationClass.getName() + "】标注");
            }
        }


        if (app != null) {
            types.add(app);
        }

        long time = System.currentTimeMillis();

        for (Class<?> type : types) {
            ioc.getIocClassLoader().appendModule(type);
        }


        time = System.currentTimeMillis();

        //Injectors

        log.debug("正在进行注入...");

        OrderedList<Pair> injectors = new OrderedList<Pair>();
        for(Class<?> type : types){
            IocObject module = ioc.get(new ZoomIocKey(type));

            IocClass iocClass = module.getIocClass();

            IocField[] fields = iocClass.getIocFields();
            if(fields!=null && fields.length >0){
                for(IocInjector injector : fields){
                    Pair pair = new Pair(module,injector);
                    injectors.add(pair,injector.getOrder());
                }
            }

            IocMethod[] methods = iocClass.getIocMethods();
            if(methods!=null && methods.length>0){
                for(IocInjector injector : methods){
                    Pair pair = new Pair(module,injector);
                    injectors.add(pair,injector.getOrder());
                }
            }

        }

        //首先执行order<=2的
        for(Pair pair :  injectors.toList()){
            pair.injector.inject(pair.obj);
        }


       // time = System.currentTimeMillis();
        // System.out.println("==========config build 成功"+(System.currentTimeMillis()-time)+"==========");

        list.clear();
        log.debug("注入完成，正在设置完成");
        ioc.setLoadComplete();

    }


    static class Pair{
        IocInjector injector;
        IocObject obj;

        public Pair( IocObject obj,IocInjector injector) {
            this.injector = injector;
            this.obj = obj;
        }
    }

}
