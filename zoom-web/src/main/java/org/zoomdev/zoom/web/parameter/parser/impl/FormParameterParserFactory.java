package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.form.NamedFormParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.form.RequestBodyForm2BeanAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.form.RequestBodyForm2MapAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.map.PathMapParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.form.NamedFormParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.form.RequestBodyForm2BeanAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.form.RequestBodyForm2MapAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.map.PathMapParameterAdapter;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class FormParameterParserFactory extends AbsParameterParserFactory<HttpServletRequest> {

	

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected ParameterAdapter<HttpServletRequest> createAdapter(String name, Class<?> type, Type genericType, Annotation[] annotations) {
		if (isRequestBody(name, annotations)) {
			if( type == Map.class ) {
				return RequestBodyForm2MapAdapter.ADAPTER;
			}
			return RequestBodyForm2BeanAdapter.ADAPTER;
		} else if(isPathVariable(name, annotations)){
			
			return (ParameterAdapter)PathMapParameterAdapter.ADAPTER;
			
		} else {
			// 简单类型直接来
			if (Classes.isSimple(type)) {
				return NamedFormParameterAdapter.ADAPTER;
			} else if (type.isArray()) {
				return NamedFormParameterAdapter.ADAPTER;
			} else if (Collection.class.isAssignableFrom(type)) {
				return NamedFormParameterAdapter.ADAPTER;
			} else if (Map.class.isAssignableFrom(type)) {
				return NamedFormParameterAdapter.ADAPTER;
			} else {
				// 按照bean处理
				return NamedFormParameterAdapter.ADAPTER;
			}
		}

	}


}
