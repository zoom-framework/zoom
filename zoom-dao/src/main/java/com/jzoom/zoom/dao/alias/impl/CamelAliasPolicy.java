package com.jzoom.zoom.dao.alias.impl;

import com.jzoom.zoom.dao.alias.AliasPolicy;

public class CamelAliasPolicy implements AliasPolicy {
	public static final CamelAliasPolicy DEFAULT = new CamelAliasPolicy();
	@Override
	public String getAlias(String column) {
		column = column.toLowerCase();
		String[] names = column.split("_");
		StringBuilder result = new StringBuilder();
		int index = 0;
		for (String string : names) {
			if(index>0){
				char[] arr = string.toCharArray();
				arr[0] = Character.toUpperCase(arr[0]);
				result.append(arr);
			}else{
				result.append(string);
			}
			++index;
		}
		
		return result.toString();
	}

}
