package com.jzoom.zoom.dao;

import java.util.Arrays;

import com.jzoom.zoom.common.utils.Visitor;

public class Db {

	public static Dao[] daos = new Dao[100];
	public static int count = 0;
	
	private static final Object lock = new Object();
	
	public static void register(Dao dao) {
		synchronized (lock) {
			daos[count++] = dao;
		}
	}
	
	public static void visit(Visitor<Dao> visitor) {
		Dao[] copyDaos;
		int c;
		synchronized (lock) {
			c = count;
			copyDaos = Arrays.copyOf(daos, count);
		}
		for(int i=0; i < c ;++i) {
			if(copyDaos[i]!=null) {
				visitor.visit(copyDaos[i]);
			}
		}
	}
	


	public static synchronized void unregister(Dao dao) {
		synchronized (lock) {
			for(int i=0; i < count; ++i) {
				if(daos[i] == dao) {
					for(int j= i+1; j < count; ++j) {
						daos[j-1] = daos[j];
					}
					break;
				}
			}
			daos[--count] = null;
		}
	}
	
	
}
