package com.jzoom.zoom.dao.adapters;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PrepareStatement 适配器
 * @author jzoom
 *
 */
public interface StatementAdapter {
	void adapt(PreparedStatement statement, int index, Object value) throws SQLException;
}
