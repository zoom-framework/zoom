package org.zoomdev.zoom.dao;

import javax.sql.DataSource;

public interface DataSourceProvider {

    DataSource getDataSource();
}
