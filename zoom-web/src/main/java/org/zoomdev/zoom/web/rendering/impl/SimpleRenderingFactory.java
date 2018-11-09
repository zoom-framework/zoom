package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.annotations.JsonResponse;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.rendering.RenderingFactory;

import java.lang.reflect.Method;

public class SimpleRenderingFactory implements RenderingFactory{
	
	protected static Rendering viewRendering = new ViewRendering();
	
	private JsonRendering jsonRendering = new JsonRendering();
	
	private TemplateRendering templateRendering;

	public SimpleRenderingFactory(TemplateRendering templateRendering) {
		this.templateRendering = templateRendering;
	}
	
	@Override
	public Rendering createRendering(Class<?> targetClass, Method method) {
		return new GroupRendering(viewRendering, createRendering2(targetClass, method));
	}
	
	
	public Rendering createRendering2(Class<?> targetClass, Method method) {
		if(targetClass.isAnnotationPresent(JsonResponse.class) || method.isAnnotationPresent(JsonResponse.class)) {
			return jsonRendering;
		}
		return templateRendering;
	}
	
}
