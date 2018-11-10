package org.zoomdev.zoom.dao.driver.mysql;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.driver.AbsDriver;

public class MysqlDriver  extends AbsDriver{

	@Override
	public StringBuilder protectColumn(StringBuilder sb, String name) {
		if(name.contains(".")){
			sb.append(name);
			return sb;
		}
		return sb.append('`').append(name).append('`');
	}

	@Override
	public String protectColumn(String name) {
		int n ;
		if( (n = name.indexOf(".")) > 0  ){
			String table = name.substring(0,n);
			String column = name.substring(n+1);
			return new StringBuilder().append('`').append(table).append("`.`").append(column).append('`').toString();
		}
		return new StringBuilder().append('`').append(name).append('`').toString();
	}

	@Override
	public StatementAdapter get(Class<?> dataClass, Class<?> columnClass) {
		return super.get(dataClass, columnClass);
	}

	@Override
	public StringBuilder buildPage(StringBuilder sql, int position, int size) {
		return sql.append(" LIMIT ").append(position).append(',').append(size);
	}


	public void insertOrUpdate(StringBuilder sb,List<Object> values,String tableName,Map<String, Object> data,String...unikeys) {
		StringBuilder signs = new StringBuilder();
		sb.append("INSERT INTO ")
		.append(tableName)
		.append(" (");
		boolean first = true;
		for (Entry<String, Object> entry : data.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if(first){
				first = false;
			}else{
				signs.append(',');
				sb.append(',');
			}
			sb.append(key);
			signs.append('?');
			values.add(value);
		}
		sb.append(") VALUES (").append(signs).append(") ON DUPLICATE KEY UPDATE ");
		first = true;
		Set<String> keySet = new HashSet<String>();
		for (String string : unikeys) {
			keySet.add(string);
		}
		for (Entry<String, Object> entry : data.entrySet()) {
			String key = entry.getKey();
			if(keySet.contains(key)){
				continue;
			}
			if(first){
				first = false;
			}else{
				sb.append(',');
			}
			sb.append(key).append("=?");
			values.add(entry.getValue());
		}
	}

	@Override
	public int page2position(int page, int size) {
		// TODO Auto-generated method stub
		return 0;
	}



}
