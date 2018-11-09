package com.jzoom.zoom.web.utils;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.common.utils.CachedClasses;

public class RequestUtils {
	
	/**
	 * 
	 * 取出request中的所有Attribute
	 * @param request
	 * @return
	 */
	public static Map<String, Object> getAttributes(HttpServletRequest request){
		assert(request!=null);
		Map<String, Object> data = new HashMap<String, Object>();
		merge(data, request);
		return data;
	}
	
	/**
	 * 将request中的所有attribute合并到一个map中
	 * @param data
	 * @param request
	 */
	public static void merge(Map<String, Object> data,HttpServletRequest request) {
		assert(data!=null && request!=null);
		Enumeration<String> enumeration = request.getAttributeNames();
		while(enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			data.put(key, request.getAttribute(key));
		}
	}
	
	/**
	 * 将request转成map
	 * @param request
	 * @return
	 */
	public static Map<String, Object> getParameters(HttpServletRequest request){
		Map<String, String[]> params = request.getParameterMap();
		Map<String, Object> data = new HashMap<String, Object>();
		for (Entry<String, String[]> entry : params.entrySet()) {
			data.put(entry.getKey(), entry.getValue()[0]);
		}
		return data;
	}
	
	
	/**
	 * 整个request转成一个bean
	 * @param request
	 * @param classOfT
	 * @return
	 */
	public static <T> T getParameters(HttpServletRequest request,Class<?> classOfT) {
	//	BeanUtils.setProperty(bean, name, name);
		return null;
	}

	public static void toBean(HttpServletRequest request, Object target) {
		assert(target!=null);
		
		Field[] fields = CachedClasses.getFields(target.getClass());
		for (Field field : fields) {
			Object value = request.getParameter(field.getName());
			try {
				field.set(target, Caster.to(value, field.getType()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		
	}

}
