package org.zoomdev.zoom.dao.adapters;

import java.sql.ResultSet;

public interface ResultSetAdapterFactory {
    ResultSetAdapter<?> create(ResultSet rs);
}
