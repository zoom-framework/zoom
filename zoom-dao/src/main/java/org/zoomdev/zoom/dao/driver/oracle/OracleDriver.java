package org.zoomdev.zoom.dao.driver.oracle;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.zoomdev.zoom.dao.driver.AbsDriver;

public class OracleDriver extends AbsDriver {

	@Override
	public StringBuilder buildPage(StringBuilder sql, int position, int size) {
		return sql.insert(0, "SELECT * FROM(SELECT A.*, rownum r FROM (").append(") A WHERE rownum <= ")
		.append(position + size).append(" ) B WHERE r > ").append(position);
	}

	@Override
	public int position2page(int position, int size) {
		++position;
		if (position % size == 0) {
			return position / size;
		}
		return position / size + 1;
	}
	
	/**
	 * 系统的position从0开始,page从1开始
	 */
	@Override
	public int page2position(int page, int size) {
	
		return 0;
	}

	@Override
	public void insertOrUpdate(StringBuilder sb,List<Object> values,String tableName,Map<String, Object> data,String...unikeys) {

		sb.append("MERGE INTO").append(' ').append(tableName).append(" T1 ")
		.append("USING (SELECT ");

		
		boolean first = true;
		for (Entry<String, Object> entry  : data.entrySet()) {
			String key = entry.getKey();
			
			
			if(first){
				first = false;
			}else{
				sb.append(',');
			}
			sb.append("? AS ").append(key);
			values.add(entry.getValue());
		}
		
		Set<String> keys = new HashSet<String>();
		sb.append(" FROM DUAL) T2 ON ( ");
		
		first = true;
		for (String key : unikeys) {
			
			if(first){
				first = false;
			}else{
				sb.append(" AND ");
			}
			
			sb.append("T1.").append(key).append("=").append("T2.").append(key);
			keys.add(key);
		}
		
		
		sb.append(") ").append("WHEN MATCHED THEN UPDATE SET ");
		
		
		first = true;
		for (Entry<String, Object> entry  : data.entrySet()) {
			String key = entry.getKey();
			if(keys.contains(key)){
				continue;
			}
			
			if(first){
				first = false;
			}else{
				sb.append(',');
			}
			sb.append("T1.").append(key).append("=?");
			values.add(entry.getValue());
		}
		
		
		sb.append(" WHEN NOT MATCHED THEN INSERT (");
		
		first = true;
		for (Entry<String, Object> entry  : data.entrySet()) {
			String key = entry.getKey();
			
			if(first){
				first = false;
			}else{
				sb.append(',');
			}
			sb.append("T1.").append(key);
			values.add(entry.getValue());
		}
		
		sb.append(") VALUES (");
		
		first = true;
		for (int i =0 ,c =data.size(); i < c; ++i) {
			if(first){
				first = false;
			}else{
				sb.append(",");
			}
			sb.append("?");
		}
		
		sb.append(")");
	}

}
