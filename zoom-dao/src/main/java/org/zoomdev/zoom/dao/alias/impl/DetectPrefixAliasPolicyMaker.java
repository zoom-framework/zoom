package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.dao.alias.AliasPolicy;
import org.zoomdev.zoom.dao.alias.AliasPolicyMaker;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 自动检测字段的前缀，并以驼峰式来重命名 {@link AliasPolicyMaker}
 * 这个是针对单表的
 * 
 * @author jzoom
 *
 */
public class DetectPrefixAliasPolicyMaker implements AliasPolicyMaker {

	public static final DetectPrefixAliasPolicyMaker DEFAULT = new DetectPrefixAliasPolicyMaker();
	
	
	public DetectPrefixAliasPolicyMaker() {

	}

//	@Override
//	public NameAdapter getColumnAliasPolicy(TableMeta table) {
//
//		if (aliasPolicy != null) {
//			Map<String, String> map = new HashMap<String, String>();
//			for (ColumnMeta columnInfo : table.getColumns()) {
//				map.put(aliasPolicy.getAlias(columnInfo.getName()), columnInfo.getName());
//			}
//			return new MapNameAdapter(aliasPolicy, map);
//		} else {
//			return CamelNameAdapter.ADAPTER;
//		}
//
//	}
	
//	private String findNotEmpty() {
//		
//	}

	// 这个逻辑如果数据库字段比较规范是够用的，不够再说
	@Override
	public AliasPolicy getAliasPolicy(String[] names) {
		Map<String, MutableInt> countMap = new LinkedHashMap<String, MutableInt>();

		for (String name :names) {
			String[] arr = name.split("_");
			String prefixThisColumn = arr[0];
			MutableInt value = countMap.get(prefixThisColumn);
			if (value == null) {
				value = new MutableInt(1);
				countMap.put(prefixThisColumn, value);
			} else {
				value.add(1);
			}
		}
		AliasPolicy aliasPolicy = null;
		// 只有最大的为第一个的才行
		MutableInt first = null;
		String key = null;
		if (countMap.size() == 1) {
			key = countMap.keySet().iterator().next();
			aliasPolicy = new PrefixAliasPolicy(new StringBuilder(key).append("_").toString());
		} else {
			for (Entry<String, MutableInt> entry : countMap.entrySet()) {
				if (first == null) {
					first = entry.getValue();
					key = entry.getKey();
				} else {
					if (first.intValue() > entry.getValue().intValue()) {
						aliasPolicy = new PrefixAliasPolicy(new StringBuilder(key).append("_").toString());
					}
					break;
				}
			}
		}

		if (aliasPolicy != null) {
			return aliasPolicy;
		}

		return CamelAliasPolicy.DEFAULT;
	}

}
