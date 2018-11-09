package com.jzoom.zoom.dao.alias.impl;

import com.jzoom.zoom.dao.alias.AliasPolicy;

public class ToLowerCaseAiias implements AliasPolicy{

	@Override
	public String getAlias(String column) {
		return column.toLowerCase();
	}

}
