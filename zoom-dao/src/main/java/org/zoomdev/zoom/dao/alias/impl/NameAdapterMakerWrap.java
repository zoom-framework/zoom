package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.dao.adapters.NameAdapter;
import org.zoomdev.zoom.dao.alias.NameAdapterFactory;

public class NameAdapterMakerWrap implements NameAdapterFactory {
	
	private NameAdapter aliasPolicy;
	

	public NameAdapterMakerWrap(NameAdapter aliasPolicy) {
		this.aliasPolicy = aliasPolicy;
	}

	@Override
	public NameAdapter getNameAdapter(String tables) {
		return aliasPolicy;
	}

	@Override
	public NameAdapter getNameAdapter(String[] tables) {
		return aliasPolicy;
	}

}
