package com.jzoom.zoom.common.lock;

public class LockUtils {
	
	private static final int LOCK_COUNT = 10;
	
	private static final Object[] LOCKS;
	
	static {
		LOCKS = new Object[LOCK_COUNT];
		for(int i=0; i < LOCK_COUNT ;++i) {
			LOCKS[i]= new Object();
		}
	}
	
	public static Object getLock(Object key) {
		assert(key!=null);
		return LOCKS[Math.abs(key.hashCode() % LOCK_COUNT)];
	}

}
