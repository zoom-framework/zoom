package com.jzoom.zoom.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.RecordVisitor;


public class RecordBuilder {
	
	
	public static final Record buildOne(ResultSet rs) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Record map = new Record();
		for (int i=1; i<=columnCount; i++) {
			int type = rsmd.getColumnType(i);
			String name = rsmd.getColumnName(i);
			Object value;
			if (type < Types.BLOB)
				value = rs.getObject(i);
			else if (type == Types.CLOB)
				value = handleClob(rs.getClob(i));
			else if (type == Types.NCLOB)
				value = handleClob(rs.getNClob(i));
			else if (type == Types.BLOB)
				value = handleBlob(rs.getBlob(i));
			else
				value = rs.getObject(i);
			
			map.put(name, value);
		}
		
		return map;
	}
	
	public static final void visit(ResultSet rs,RecordVisitor visitor) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String[] labelNames = new String[columnCount + 1];
		int[] types = new int[columnCount + 1];
		buildLabelNamesAndTypes(rsmd, labelNames, types);
		while (rs.next()) {
			Record map = new Record();
			for (int i=1; i<=columnCount; i++) {
				Object value;
				if (types[i] < Types.BLOB)
					value = rs.getObject(i);
				else if (types[i] == Types.CLOB)
					value = handleClob(rs.getClob(i));
				else if (types[i] == Types.NCLOB)
					value = handleClob(rs.getNClob(i));
				else if (types[i] == Types.BLOB)
					value = handleBlob(rs.getBlob(i));
				else
					value = rs.getObject(i);
				
				map.put(labelNames[i], value);
			}
			visitor.visit(map);
		}
	}
	
	public static final Record build(int columnCount,ResultSet rs,int[] types,String[] labelNames) throws SQLException{
		Record map = new Record();
		for (int i=1; i<=columnCount; i++) {
			Object value;
			if (types[i] < Types.BLOB)
				value = rs.getObject(i);
			else if (types[i] == Types.CLOB)
				value = handleClob(rs.getClob(i));
			else if (types[i] == Types.NCLOB)
				value = handleClob(rs.getNClob(i));
			else if (types[i] == Types.BLOB)
				value = handleBlob(rs.getBlob(i));
			else
				value = rs.getObject(i);
			
			map.put(labelNames[i], value);
		}
		return map;
	}
	
	
	public static final List<Record> build(ResultSet rs) throws SQLException{
		List<Record> result = new ArrayList<Record>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String[] labelNames = new String[columnCount + 1];
		int[] types = new int[columnCount + 1];
		buildLabelNamesAndTypes(rsmd, labelNames, types);
		while (rs.next()) {
			result.add(build(columnCount, rs, types, labelNames));
		}
		return result;
	}
	public static byte[] handleBlob(Blob blob) throws SQLException {
		if (blob == null)
			return null;
		
		InputStream is = null;
		try {
			is = blob.getBinaryStream();
			byte[] data = new byte[(int)blob.length()];		// byte[] data = new byte[is.available()];
			is.read(data);
			is.close();
			return data;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			try {is.close();} catch (IOException e) {throw new RuntimeException(e);}
		}
	}
	
	public static String handleClob(Clob clob) throws SQLException {
		if (clob == null)
			return null;
		
		Reader reader = null;
		try {
			reader = clob.getCharacterStream();
			char[] buffer = new char[(int)clob.length()];
			reader.read(buffer);
			return new String(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			try {reader.close();} catch (IOException e) {throw new RuntimeException(e);}
		}
	}
	
	private static final void buildLabelNamesAndTypes(ResultSetMetaData rsmd, String[] labelNames, int[] types) throws SQLException {
		for (int i=1; i<labelNames.length; i++) {
			labelNames[i] = rsmd.getColumnLabel(i);
			types[i] = rsmd.getColumnType(i);
		}
	}
}
