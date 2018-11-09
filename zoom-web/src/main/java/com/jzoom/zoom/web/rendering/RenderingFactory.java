package com.jzoom.zoom.web.rendering;

import java.lang.reflect.Method;

public interface RenderingFactory {
	Rendering createRendering( Class<?> targetClass, Method method );
}
