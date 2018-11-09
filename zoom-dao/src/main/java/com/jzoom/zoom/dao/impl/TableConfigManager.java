package com.jzoom.zoom.dao.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.driver.DbStructFactory;

public class TableConfigManager {
	
	private Map<String, TableConfig> map = new ConcurrentHashMap<String, TableConfig>();
	
	private DbStructFactory factory;
	
	public TableConfig getConfig(Ar ar,String table) {
		TableConfig config = map.get(table);
		if(config == null) {
			
			
			
		}
		
		return null;
		
	}
	
}
