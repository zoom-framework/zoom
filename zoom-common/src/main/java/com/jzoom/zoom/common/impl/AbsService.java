package com.jzoom.zoom.common.impl;

import com.jzoom.zoom.common.Service;

public abstract class AbsService implements Service {

	@Override
	public final void startup() throws Exception {
		synchronized (this) {
			startupSync();
		}
	}

	@Override
	public final void shutdown() {
		synchronized (this) {
			shutdownSync();
		}
	}
	
	protected abstract void startupSync()  throws Exception;

	protected abstract void shutdownSync() ;
}
