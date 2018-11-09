package com.jzoom.zoom.web.parameter.adapter;

import java.util.Enumeration;

public interface MapParameter {
	
	Object getParam(String key);
	
	
	Enumeration<String> keys();
	
}
