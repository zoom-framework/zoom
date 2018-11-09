package org.zoomdev.zoom.dao;

import java.sql.Connection;

public interface ConnectionHolder {
	
	void releaseConnection();
	
	Connection getConnection();
	
	
}
