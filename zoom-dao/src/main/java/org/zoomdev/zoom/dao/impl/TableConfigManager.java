package org.zoomdev.zoom.dao.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.zoomdev.zoom.dao.Ar;
import org.zoomdev.zoom.dao.driver.DbStructFactory;

public class TableConfigManager {
	
	private Map<String, TableConfig> map = new ConcurrentHashMap<String, TableConfig>();
	
	private DbStructFactory factory;
	
	public TableConfig getConfig(Ar ar, String table) {
		TableConfig config = map.get(table);
		if(config == null) {
			
			
			
		}
		
		return null;
		
	}
	
}
