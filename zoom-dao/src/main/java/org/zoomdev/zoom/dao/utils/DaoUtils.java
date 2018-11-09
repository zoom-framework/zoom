package org.zoomdev.zoom.dao.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zoomdev.zoom.dao.Record;

public class DaoUtils {
	public static void close(ResultSet rs) {
		if(rs!=null)try {rs.close();}catch (Exception e) {}
	}

	public static void close(PreparedStatement ps) {
		
		if(ps!=null)try {ps.close();}catch (Exception e) {}
	}

	public static void close(Connection connection) {
		if(connection!=null)try {connection.close();}catch (Exception e) {}
	}
	
	
	
	/**
	 * 将list转为map
	 * @param list
	 * @param pk
	 * @return
	 */
	public static Map<Object,Record> list2map(List<Record> list, String pk){
		if(list==null)return null;
		Map<Object, Record> map = new HashMap<Object, Record>(list.size());
		for (Record record : list) {
			map.put(record.get(pk), record);
		}
		return map;
	}
}
