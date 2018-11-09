package org.zoomdev.zoom.web.parameter.adapter.impl.map;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

public class RequestBodyMapAdapter implements ParameterAdapter<Map<String, Object>>{
	
	
	public static final ParameterAdapter<Map<String, Object>> ADAPTER = new RequestBodyMapAdapter();

	public RequestBodyMapAdapter( ) {
	}

	@Override
	public Object get(ActionContext context, Map<String, Object> data,String name,Class<?> type) {
		
		try {
			Object bean =type.newInstance();
			BeanUtils.populate(bean, data);
			return bean;
		} catch (Exception e) {
			throw new RuntimeException( String.format("初始化Bean失败,class:%s 参数:%s", type, data )  );
		}
		
	}
	
}
