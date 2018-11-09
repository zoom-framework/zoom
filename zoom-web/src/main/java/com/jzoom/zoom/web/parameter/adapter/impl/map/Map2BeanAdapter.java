package com.jzoom.zoom.web.parameter.adapter.impl.map;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.parameter.adapter.ParameterAdapter;



public class Map2BeanAdapter implements ParameterAdapter<Map<String, Object>>{
	
	
	public Map2BeanAdapter() {
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public Object get(ActionContext context, Map<String, Object> data,String name,Class<?> type) {
		Map<String, Object> value = (Map<String, Object>) data.get(name);
		if(value==null) {
			return null;
		}
		try {
			Object bean = type.newInstance();
			BeanUtils.populate(bean, value);
			return bean;
		} catch (Exception e) {
			throw new RuntimeException( String.format("初始化Bean失败,class:%s 参数:%s", type, data )  );
		}
		
		
	}
	
}