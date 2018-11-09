package org.zoomdev.zoom.dao;

/**
 * 数据库连接描述
 * @author jzoom
 *
 */
public abstract class ConnectionDescriptor {

	private String driverClass;
	
	private String user;
	
	private String password;
	
	private String server;
	
	private int port;
	
	
	public ConnectionDescriptor() {
		
	}
	


	public String getUser() {
		return user;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getDriverClass() {
		return driverClass;
	}


	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}


	public abstract String getJdbcUrl() ;

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUser(String user) {
		this.user = user;
	}


	
}
