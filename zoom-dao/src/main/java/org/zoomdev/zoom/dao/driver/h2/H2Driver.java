package org.zoomdev.zoom.dao.driver.h2;

import org.zoomdev.zoom.dao.driver.mysql.MysqlDriver;

public class H2Driver extends MysqlDriver{

	@Override
	public StringBuilder buildPage(StringBuilder sql, int position, int size) {
		return sql.append(" LIMIT ").append(position).append(',').append(size);
	}




}
