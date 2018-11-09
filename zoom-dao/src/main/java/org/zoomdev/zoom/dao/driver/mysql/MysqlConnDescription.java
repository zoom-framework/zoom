package org.zoomdev.zoom.dao.driver.mysql;

import org.zoomdev.zoom.dao.ConnectionDescriptor;

public class MysqlConnDescription extends ConnectionDescriptor {
	
	public static final int DEFAULT_PORT = 3306;
	
	private String dbName;
	
	
	public MysqlConnDescription() {
		this("localhost", DEFAULT_PORT, "zoom", "root", "root");
	}
	
	public MysqlConnDescription(String server,int port,String db,String user,String password){
		setPassword(password);
		setUser(user);
		setDriverClass("com.mysql.jdbc.Driver");
		setServer(server);
		setDbName(db);
		setPort(port);
	}
	
	public MysqlConnDescription(String server,String db,String user,String password){
		this(server, DEFAULT_PORT, db, user, password);
	}

	
	
	
	@Override
	public String getJdbcUrl() {
		return "jdbc:mysql://"+getServer()+":"+ getPort() + "/"+getDbName()+"?useUnicode=true&characterEncoding=UTF-8";
	}
	


	public String getDbName() {
		return dbName;
	}




	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	
	
	
}
