package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.dao.alias.AliasPolicy;

public class EmptyAlias implements AliasPolicy {

	@Override
	public String getAlias(String column) {
		return column;
	}

}
