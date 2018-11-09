package org.zoomdev.zoom.dao.alias.impl;

import java.util.Map;

import org.zoomdev.zoom.common.utils.StrKit;
import org.zoomdev.zoom.dao.adapters.NameAdapter;

/**
 * 通过两个map来确定改名规则
 * 
 * 
 * 
 * @author jzoom
 *
 */
public class MapNameAdapter implements NameAdapter {
	Map<String, String> field2columnMap;
	Map<String, String> column2fieldMap;
	Map<String, String> field2AsMap;
	Map<String, String> column2OrgFieldMap;
	
	
	public MapNameAdapter(Map<String, String> field2columnMap,
						  Map<String, String> column2fieldMap,
						  Map<String, String> field2AsMap,
						  Map<String, String> column2OrgFieldMap) {
		super();
		this.field2columnMap = field2columnMap;
		this.column2fieldMap = column2fieldMap;
		this.field2AsMap = field2AsMap;
		this.column2OrgFieldMap = column2OrgFieldMap;
	}

	@Override
	public String getOrgFieldName(String column) {
		return column2OrgFieldMap.get(column);
	}

	@Override
	public String getSelectColumn(String field) {
		return field2AsMap.get(field);
	}

	@Override
	public String getFieldName(String column) {
		String field = column2fieldMap.get(column);
		if(field == null) {
			return column;
		}
		return field;
	}



	@Override
	public String getColumnName(String field) {
		String column = field2columnMap.get(field);
		if(column == null) {
			return field;
		}
		return column;
	}

	@Override
	public String getSelectField(String column) {
		return StrKit.toCamel(column);
	}

}
