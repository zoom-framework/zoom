package org.zoomdev.zoom.aop.utils;

import org.zoomdev.zoom.common.exceptions.ZoomException;

public class AopUtils {

    /**
     * 获取未增强之前的类
     * @param type
     * @param classLoader
     * @return
     */
    public static Class<?> getOrginalClass(Class<?> type,ClassLoader classLoader){
        if(type.getSimpleName().endsWith("$Enhance")){
            try {
                String className = type.getName();
                className = className.substring(0,className.length()-8);
                type = Class.forName(className,false,classLoader);
            } catch (ClassNotFoundException e) {
                throw new ZoomException();
            }
        }
        return type;
    }
}
