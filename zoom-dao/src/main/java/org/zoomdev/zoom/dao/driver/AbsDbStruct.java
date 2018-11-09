package org.zoomdev.zoom.dao.driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.dao.Ar;
import org.zoomdev.zoom.dao.ConnectionExecutor;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;
import org.zoomdev.zoom.dao.utils.DaoUtils;

public abstract class AbsDbStruct implements DbStructFactory{
	protected String getQueryTableMetaStatement(String table) {
		return "select * from " + table + " where 1=2";
	}
	
	@Override
	public TableMeta getTableMeta(Ar ar, final String table) {
		TableMeta tableMeta = ar.execute(new ConnectionExecutor() {

			@SuppressWarnings("unchecked")
			@Override
			public TableMeta execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet rs = null;
				try {
					statement = connection.prepareStatement(getQueryTableMetaStatement(table));
					rs = statement.executeQuery();
					ResultSetMetaData data = rs.getMetaData();
					List<ColumnMeta> columnMetas = new ArrayList<ColumnMeta>(data.getColumnCount());
					for (int i = 1, c = data.getColumnCount(); i <= c; ++i) {
						ColumnMeta column = new ColumnMeta();
						String className = data.getColumnClassName(i);
						column.setDataType(Classes.forName(className));
						column.setName(data.getColumnName(i));
						column.setType(data.getColumnType(i));
						column.setRawType(data.getColumnTypeName(i));
						columnMetas.add(column);
					}

					TableMeta meta = new TableMeta();
					meta.setName(table);
					meta.setColumns(columnMetas.toArray(new ColumnMeta[columnMetas.size()]));
					return meta;
				} finally {
					DaoUtils.close(statement);
					DaoUtils.close(rs);
				}

			}

			
		});


		return tableMeta;
	}


	/**
	 * 清除缓存
	 */
	public void clearCache(){

	}
	
}
