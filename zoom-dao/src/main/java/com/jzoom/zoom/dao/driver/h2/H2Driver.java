package com.jzoom.zoom.dao.driver.h2;

import com.jzoom.zoom.dao.driver.mysql.MysqlDriver;

public class H2Driver extends MysqlDriver{

	@Override
	public StringBuilder buildPage(StringBuilder sql, int position, int pageSize) {
		return sql.append(" LIMIT ").append(position).append(',').append(pageSize);
	}




}
