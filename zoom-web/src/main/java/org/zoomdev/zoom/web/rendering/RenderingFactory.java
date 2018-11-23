package org.zoomdev.zoom.web.rendering;

import java.lang.reflect.Method;

public interface RenderingFactory {
    RenderingChain createRendering(Class<?> targetClass, Method method);
    RenderingChain createExceptionRendering( Class<?> targetClass, Method method );

    void add(int index,Rendering rendering);
    void addError(int index,Rendering rendering);
    void add(Rendering rendering);
    void addError(Rendering rendering);
}
