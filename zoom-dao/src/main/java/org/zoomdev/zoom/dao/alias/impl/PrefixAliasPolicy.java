package org.zoomdev.zoom.dao.alias.impl;

public class PrefixAliasPolicy extends CamelAliasPolicy {

	private String prefix;

	public PrefixAliasPolicy(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String getAlias(String column) {
		if(column.startsWith(prefix)) {
			column = column.substring(prefix.length());
		}
		
		return super.getAlias(column);
		
	}

	

}
