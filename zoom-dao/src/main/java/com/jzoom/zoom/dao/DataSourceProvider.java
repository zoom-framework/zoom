package com.jzoom.zoom.dao;

import javax.sql.DataSource;

public interface DataSourceProvider {

	DataSource getDataSource();
}
