package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.caster.ValueCaster;
import com.jzoom.zoom.common.filter.Filter;
import com.jzoom.zoom.dao.AutoField;
import com.jzoom.zoom.dao.DaoException;
import com.jzoom.zoom.dao.Entity;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.adapters.EntityField;
import com.jzoom.zoom.dao.adapters.NameAdapter;
import com.jzoom.zoom.dao.adapters.StatementAdapter;
import com.jzoom.zoom.dao.driver.SqlDriver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BuilderKit {

    public static void buildSelect() {

    }


    private static void appendValue(List<Object> values, Object value,
                                    List<StatementAdapter> insertFields,
                                    EntityField entityField, StringBuilder sql, String[] specialValues, int index, SqlDriver driver) {
        values.add(value);
        insertFields.add(entityField.getStatementAdapter());
        driver.protectColumn(sql, entityField.getColumnName());
        specialValues[index] = "?";
    }

	public static void buildInsert(
			StringBuilder sql,
            List<StatementAdapter> insertFields,
			List<Object> values,
			SqlDriver driver,
			Entity entity,
			Object data,
            Filter<EntityField> filter,
            boolean ignoreNull
			) {

        EntityField[] fields = entity.getEntityFields();
		sql.append("INSERT INTO ").append(entity.getTable()).append(" (");
		boolean first = true;
        int index = 0;
        String[] specialValues = new String[fields.length];
		for (EntityField entityField : fields) {
            //插入数据,如果有忽略掉其他判断
            AutoField autoField = entityField.getAutoField();
            Object value;
            if (autoField != null) {
                String specialValue;
                if ((specialValue = autoField.getSqlInsert(data, entityField)) != null) {
                    specialValues[index] = specialValue;
				} else if ((value = autoField.generageValue(data, entityField)) != null) {
                    if (first) {
                        first = false;
                    } else {
                        sql.append(COMMA);
                    }
                    appendValue(values, value, insertFields, entityField, sql, specialValues, index, driver);
                } else {
                    // 都没有？ 不需要处理
                    continue;
                }

            } else {
                if (filter == null || filter.accept(entityField)) {
                    value = entityField.get(data);
                    if (value == null && ignoreNull) {
                        continue;
                    }
                    if (first) {
                        first = false;
                    } else {
                        sql.append(COMMA);
                    }
                    appendValue(values, value, insertFields, entityField, sql, specialValues, index, driver);
                }
            }
            ++index;
        }
        sql.append(") VALUES (");
        first = true;
        for (int i = 0; i < index; ++i) {
            String value = specialValues[i];
            if (value == null) continue;
            if (first) {
                first = false;
            } else {
                sql.append(",");
            }
            sql.append(value);
		}
		sql.append(')');

	}




	/**
	 * 构建插入语句
	 * @param sql
	 * @param values
	 * @param driver
	 * @param table
	 * @param record
	 */
	public static void buildInsert(StringBuilder sql,List<Object> values,SqlDriver driver, String table,Record record) {

		sql.append("INSERT INTO ").append(table).append(" (");
		boolean first = true;
		for (Entry<String, Object> entry : record.entrySet()) {
			Object value = entry.getValue();
			String name = entry.getKey();
			if (first) {
				first = false;
			} else {
				sql.append(COMMA);
			}
			values.add(value);
			driver.protectColumn(sql,name);
		}
		//?
		join(sql.append(") VALUES ("),record.size()).append(')');

    }

	public static final char QM = '?';  //Question Mark
	public static final char COMMA = ',';  //comma
	/**
     * 问号    组合成  ?,?
	 * @param sql
	 * @param size   问号个数
     * @return
	 */
	public static StringBuilder join(StringBuilder sql, int size) {
		for(int i=0; i < size; ++i) {
			if(i>0) {
				sql.append(COMMA);
			}
			sql.append(QM);
		}
		return sql;
	}

	private static final Log log = LogFactory.getLog(BuilderKit.class);

	public static PreparedStatement prepareStatementForAdapters(
			Connection connection,
			String sql,
			List<Object> values,
			List<StatementAdapter> adapters) throws SQLException {
		if(log.isDebugEnabled()) {
			log.debug(String.format(sql.replace("?", "'%s'"), values.toArray(new Object[values.size()])));
		}

		PreparedStatement ps = connection.prepareStatement(sql);
		for(int index = 0,c=values.size(); index < c; ++index) {
			StatementAdapter adapter = adapters.get(index);
			adapter.adapt(ps,index+1,values.get(index));
		}
		return ps;
	}



    public static <T> T build(Entity entity,
                              EntityField[] entityAdapters,
                              int entityAdaptersCount,
                              ResultSet rs) throws SQLException {
        Object data = entity.newInstance();
        ResultSetMetaData metaData = rs.getMetaData();
        for(int i = 0; i < entityAdaptersCount; ++i){
            EntityField adapter = entityAdapters[i];
            try{

                Object r = rs.getObject(i+1);
//                System.err.println( metaData.getColumnLabel(i+1) + ":" + r.getClass()+" "+ adapter.getFieldName() +":"+adapter.getField().getType() );
                adapter.set(data,adapter.getFieldValue(r));
            }catch (Exception e){
                throw new DaoException("不能设置查询结果"+adapter.getFieldName(),e);
            }

        }
        return (T)data;
    }

    public  static <T> List<T> buildList(Entity entity,
                                         EntityField[] entityAdapters,
                                         int entityAdaptersCount,
                                         ResultSet rs) throws SQLException {

        List<T> list = new ArrayList<T>();
        while(rs.next()){
            T data = build(entity,entityAdapters,entityAdaptersCount,rs);
            list.add(data);
        }

        return list;

    }


	public static PreparedStatement prepareStatement(
            Connection connection,
            String sql,
            List<Object> values) throws SQLException {

        log.info(String.format(sql.replace("?", "'%s'"),
                values.toArray(new Object[values.size()])));

        PreparedStatement ps = connection.prepareStatement(sql);
        for(int index = 1,c=values.size(); index <= c; ++index) {
            ps.setObject(index, values.get(index-1));
        }
        return ps;
    }

    public static PreparedStatement prepareStatement(
            Connection connection,
            String sql,
            List<Object> values,
            List<StatementAdapter> adapters) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(sql);
        for (int index = 0, c = values.size(); index < c; ++index) {
            StatementAdapter adapter = adapters.get(index);
            adapter.adapt(ps, index + 1, values.get(index));
        }
        return ps;
    }
	public static void prepareStatement(
            PreparedStatement ps,
			List<Object> values,
			List<StatementAdapter> adapters) throws SQLException {

		for(int index = 0,c=values.size(); index < c; ++index) {
		    StatementAdapter adapter = adapters.get(index);
            adapter.adapt(ps,index+1,values.get(index));
		}
	}

    private static ValueCaster blobCaster;
	private static ValueCaster clobCaster;
	static {

        blobCaster = Caster.wrap(Blob.class, String.class);
		clobCaster = Caster.wrap(Clob.class, String.class);

    }
	public static final Record buildOne(ResultSet rs,List<String> names) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Record map = new Record();
		for (int i=1; i<=columnCount; i++) {
			int type = rsmd.getColumnType(i);
			String name = rsmd.getColumnName(i);
			map.put( names.get(i-1) ,  getValue(type, rs, i) );
		}

		return map;
	}
	public static final Record buildOne(ResultSet rs,NameAdapter policy) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Record map = new Record();
		for (int i=1; i<=columnCount; i++) {
			int type = rsmd.getColumnType(i);
			String name = rsmd.getColumnName(i);
			map.put( policy.getSelectField(name) ,  getValue(type, rs, i) );
		}

        return map;
    }

    private static Object getValue(int type, ResultSet rs, int i) throws SQLException {
		if (type < Types.BLOB)
			return rs.getObject(i);
		else if (type == Types.CLOB)
			return clobCaster.to(rs.getClob(i));
		else if (type == Types.NCLOB)
			return clobCaster.to(rs.getNClob(i));
		else if (type == Types.BLOB)
			return blobCaster.to(rs.getBlob(i));
		else
			return rs.getObject(i);
	}

    public static final Record build(int columnCount, ResultSet rs, int[] types, String[] labelNames) throws SQLException {
		Record map = new Record();
		for (int i=1; i<=columnCount; i++) {
			map.put(labelNames[i], getValue(types[i],rs,i));
		}
		return map;
	}

    public static final Record build(int columnCount, ResultSet rs, String[] labelNames) throws SQLException {
		Record map = new Record();
		for (int i=1; i<=columnCount; i++) {
			map.put(labelNames[i],  rs.getObject(i));
		}
		return map;
	}

	public static final List<Record> build(ResultSet rs,String[] labelNames) throws SQLException{
		List<Record> result = new ArrayList<Record>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		int[] types = new int[columnCount + 1];
		buildTypes(rsmd, types);
		while (rs.next()) {
			result.add(build(columnCount, rs, types, labelNames));
		}
		return result;
	}

	public static final List<Record> build(ResultSet rs,NameAdapter policy) throws SQLException{
		List<Record> result = new ArrayList<Record>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String[] labelNames = new String[columnCount + 1];
		int[] types = new int[columnCount + 1];
		buildLabelNamesAndTypes(rsmd, labelNames, types,policy);
		while (rs.next()) {
			result.add(build(columnCount, rs, types, labelNames));
		}
		return result;
	}

	private static final void buildTypes(ResultSetMetaData rsmd, int[] types) throws SQLException {
		for (int i=1; i<types.length; i++) {
			types[i] = rsmd.getColumnType(i);
		}
	}

	private static final void buildLabelNamesAndTypes(ResultSetMetaData rsmd, String[] labelNames, int[] types, NameAdapter policy) throws SQLException {
		if(policy!=null) {
			for (int i=1; i<labelNames.length; i++) {
				labelNames[i] = policy.getSelectField(rsmd.getColumnLabel(i));
				types[i] = rsmd.getColumnType(i);
			}
		}else {
			for (int i=1; i<labelNames.length; i++) {
				labelNames[i] = rsmd.getColumnLabel(i);
				types[i] = rsmd.getColumnType(i);
			}
		}
	}

    public static void buildUpdate(
            StringBuilder sql,
            String tableName,
            EntityField[] updatedFields,
            int count
    ) {
        sql.append("UPDATE ").append(tableName);
        boolean first = true;
        for (int i = 0; i < count; ++i) {
            EntityField field = updatedFields[i];
            if (first) {
                first = false;
                sql.append(" SET ");
            } else {
                sql.append(',');
            }
            sql.append(field.getColumnName()).append("=?");
        }
    }

    public static void buildUpdate(
            Entity entity,
            StringBuilder sql,
            List<Object> values,
            SqlDriver driver,
            StringBuilder where,
            Object record,
            Filter<EntityField> filter,
            List<StatementAdapter> adapters,
            boolean ignoreNull
    ) {
        sql.append("UPDATE ").append(entity.getTable());
        boolean first = true;
        int index = 0;
        for (EntityField field : entity.getEntityFields()) {
            if (filter == null || filter.accept(field)) {
                Object value = field.get(record);
                if (value == null && ignoreNull) continue;
                if (first) {
                    first = false;
                    sql.append(" SET ");
                } else {
                    sql.append(COMMA);
                }
                values.add(index, value);
                adapters.add(index, field.getStatementAdapter());
                ++index;
                driver.protectColumn(sql, field.getColumnName()).append("=?");
            }
        }
        if (index == 0) {
            throw new DaoException("至少更新一个字段");
        }
        sql.append(where);
    }


    public static void buildUpdate(
            StringBuilder sql,
            List<Object> values,
            SqlDriver driver,
            String table,
            StringBuilder where,
            Record record
    ) {
		sql.append("UPDATE ").append(table);
		boolean first = true;
		int index = 0;
		for (Entry<String, Object> entry : record.entrySet()) {
			Object value = entry.getValue();
			if (first) {
				first = false;
				sql.append(" SET ");
			} else {
				sql.append(COMMA);
			}
			values.add( index++,value);
			driver.protectColumn(sql,entry.getKey()).append("=?");
		}

		sql.append(where);
	}

    /**
	 * 构建delete语句
	 */
    public static void buildDelete(
            StringBuilder sql,
            String table,
            StringBuilder where
    ) {
		if (where.length() <= 0) {
			throw new DaoException("Whole table delete is not valid!");
		}
		sql.append("DELETE FROM ").append(table).append(where);
	}

    public static final Pattern AS_PATTERN = Pattern.compile("([a-z_\\(\\)\\.\\[\\]]+)[\\s]+as[\\s]+([a-z_]+)", Pattern.CASE_INSENSITIVE);


    /**
	 * 将select中的as解析出来
	 * @param select
	 * @return
	 */
	public static String parseAs(String select) {
		Matcher matcher = null;
		if( (matcher =  BuilderKit.AS_PATTERN.matcher(select) ) .matches()) {
			return matcher.group(2);
		}
		return select;
	}



}
