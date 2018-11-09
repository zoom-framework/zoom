package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.annotations.JsonResponse;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.rendering.RenderingFactory;

import java.lang.reflect.Method;

public class SimpleErrorRenderingFactory implements RenderingFactory {
	
	private JsonErrorRendering jsonErrorRendering = new JsonErrorRendering();
	private TemplateRendering templateRendering;

	public SimpleErrorRenderingFactory(TemplateRendering templateRendering) {
		this.templateRendering = templateRendering;
	}

	@Override
	public Rendering createRendering(Class<?> targetClass, Method method) {
		
		return new GroupRendering(SimpleRenderingFactory.viewRendering,createRendering2(targetClass,method));
	}

	public Rendering createRendering2(Class<?> targetClass, Method method) {
		if(targetClass.isAnnotationPresent(JsonResponse.class) || method.isAnnotationPresent(JsonResponse.class)) {
			return jsonErrorRendering;
		}
		return templateRendering;
	}
}
