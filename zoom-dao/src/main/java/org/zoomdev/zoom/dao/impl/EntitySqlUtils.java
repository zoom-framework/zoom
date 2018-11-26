package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.auto.AutoField;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.zoomdev.zoom.dao.utils.DaoUtils;
import org.zoomdev.zoom.dao.validator.Validator;
import org.zoomdev.zoom.dao.validator.ValidatorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EntitySqlUtils {


    static final Pattern TABLE_AND_COLUMN_PATTERN = Pattern.compile("[a-zA-Z0-9_]+[\\s]*\\.[\\s]*[a-zA-Z0-9_]+|[a-zA-Z0-9_]+");


    public static <E> E getValue(Connection connection, SimpleSqlBuilder builder, String key, Class<E> typeOfE,boolean output) throws SQLException {
        builder.selectRaw(key);
        builder.buildSelect();
        return Caster.to(EntitySqlUtils.executeGetValue(connection, builder,output), typeOfE);
    }

    static class PatterFilter implements Filter<EntityField> {

        private Filter<String> pattern;

        PatterFilter(Filter<String> pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean accept(EntityField value) {
            return pattern.accept(value.getFieldName());
        }
    }

    /**
     * 参数顺序
     * <p>
     * ConnectionHolder
     * SimpleSqlBuilder
     * Driver
     * Entity
     * data,
     * Other properties
     */


    public static final char QM = '?';  //Question Mark
    public static final char COMMA = ',';  //comma

    public static int executeUpdate(
            final Connection connection,
            final SimpleSqlBuilder builder) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = prepareStatement(connection, builder);
            return ps.executeUpdate();
        } finally {
            DaoUtils.close(ps);
        }
    }

    public static void buildInsert(
            SimpleSqlBuilder builder,
            SqlDriver driver,
            Entity entity,
            Object data,
            Filter<EntityField> filter,
            boolean ignoreNull
    ) {

        StringBuilder sql = builder.sql;
        List<StatementAdapter> insertFields = builder.adapters;
        List<Object> values = builder.values;

        EntityField[] fields = entity.getEntityFields();
        sql.append("INSERT INTO ").append(
                driver.protectTable(entity.getTable())
        ).append(" (");
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
                } else if ((value = autoField.generateValue(data, entityField)) != null) {
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

        //在insert的时候，需要判断一下null值是否可以入库

        //

    }

    public static void buildUpdate(
            SimpleSqlBuilder builder,
            SqlDriver driver,
            Entity entity,
            Object record,
            Filter<EntityField> filter,
            boolean ignoreNull
    ) {
        StringBuilder sql = builder.sql;
        List<Object> values = builder.values;
        StringBuilder where = builder.where;

        List<StatementAdapter> adapters = builder.adapters;

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
                appendValue(values, adapters, field, index, value);
                ++index;
                driver.protectColumn(sql, field.getColumnName()).append("=?");
            }
        }
        if (index == 0) {
            throw new DaoException("至少更新一个字段");
        }
        if (where.length() > 0) {
            sql.append(" where ").append(where);
        } else {
            throw new DaoException("至少需要一个where条件");
        }

    }


    protected static void validateValue(EntityField field, Object value) {
        try {
            for (Validator validator : field.getValidators()) {
                validator.validate(value);
            }
        } catch (ValidatorException e) {
            e.setEntityField(field);
            e.setValue(value);
            String message = null;
            switch (e.getType()) {
                case ValidatorException.CAST:
                    message = String.format("%s格式错误,需要的格式为%s,而实际为%s", field.getColumnMeta().getComment(), field.getColumnMeta().getDataType(), value);
                    break;
                case ValidatorException.LENGTH:
                    message = String.format("数据%s长度过长", value);
                    break;
                case ValidatorException.NULL:
                    message = String.format("%s不能为空", field.getColumnMeta().getComment());
                    break;
            }
            e.setMessage(message);
            throw e;
        }

    }

    protected static void appendValue(
            List<Object> values,
            List<StatementAdapter> adapters,
            EntityField field,
            int index,
            Object value) {
        validateValue(field, value);
        values.add(index, value);
        adapters.add(index, field.getStatementAdapter());


    }

    public static void entityCondition(SimpleSqlBuilder builder, Entity entity, Object data) {
        // 主键
        for (EntityField adapter : entity.getPrimaryKeys()) {
            Object value = adapter.get(data);
            if (value != null) {
                builder.where(adapter.getColumnName(), value);
                builder.adapters.add(adapter);
            }
        }
    }

    private static void appendValue(List<Object> values, Object value,
                                    List<StatementAdapter> insertFields,
                                    EntityField entityField,
                                    StringBuilder sql,
                                    String[] specialValues,
                                    int index,
                                    SqlDriver driver) {
        validateValue(entityField, value);
        values.add(value);
        insertFields.add(entityField.getStatementAdapter());
        driver.protectColumn(sql, entityField.getColumnName());
        specialValues[index] = "?";
    }


    public static int executeInsert(
            Connection connection,
            final Entity entity,
            final Object data,
            final SimpleSqlBuilder builder) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = entity.prepareInsert(connection, builder.sql.toString());
            prepareStatement(ps, builder.values, builder.adapters);
            int ret = ps.executeUpdate();
            if (ret > 0) {
                entity.afterInsert(data, ps);
            }
            return ret;
        } finally {
            DaoUtils.close(ps);
        }

    }

    static PreparedStatement prepareStatement(
            Connection connection,
            SimpleSqlBuilder builder) throws SQLException {

        return prepareStatement(
                connection,
                builder.sql.toString(),
                builder.values,
                builder.adapters);
    }

    static void prepareStatement(
            PreparedStatement ps,
            List<Object> values,
            List<StatementAdapter> adapters) throws SQLException {

        for (int index = 0, c = values.size(); index < c; ++index) {
            StatementAdapter adapter = adapters.get(index);
            adapter.adapt(ps, index + 1, values.get(index));
        }
    }

    static PreparedStatement prepareStatement(
            Connection connection,
            String sql,
            List<Object> values,
            List<StatementAdapter> adapters) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(sql);
        for (int index = 0, c = values.size(), m = adapters.size(); index < c; ++index) {
            if (index >= m) {

                ps.setObject(index + 1, values.get(index));

            } else {
                StatementAdapter adapter = adapters.get(index);
                adapter.adapt(ps, index + 1, values.get(index));
            }

        }
        return ps;
    }


    static <T> List<T> buildList(
            ResultSet rs,
            Entity entity,
            List<EntityField> entityFields
    ) throws SQLException {

        List<T> list = new ArrayList<T>();
        while (rs.next()) {
            T data = buildRecord(rs, entity, entityFields);
            list.add(data);
        }

        return list;

    }


    static <T> T buildRecord(
            ResultSet rs,
            Entity entity,
            List<EntityField> entityFields
    ) throws SQLException {
        Object data = entity.newInstance();
        for (int i = 0, c = entityFields.size(); i < c; ++i) {
            EntityField entityField = entityFields.get(i);
            assert (entityField != null);
            try {
                Object r = rs.getObject(i + 1);
                entityField.set(data, entityField.getFieldValue(r));
            } catch (Exception e) {
                throw new DaoException("不能设置查询结果" + entityField.getFieldName(), e);
            }

        }
        return (T) data;
    }

    static void buildSelect(
            SimpleSqlBuilder builder,
            Entity entity,
            Filter<EntityField> filter,
            List<EntityField> entityFields) {
        // build select
        if (entityFields.size() > 0) {
            for (EntityField field : entityFields) {
                builder.selectRaw(field.getSelectColumnName());
            }
            return;
        }
        for (EntityField field : entity.getEntityFields()) {
            if (filter == null || filter.accept(field)) {
                builder.selectRaw(field.getSelectColumnName());
                entityFields.add(field);
            }
        }
        if (entityFields.size() == 0) {
            throw new DaoException("必须指定至少一个selet字段");
        }

    }


    static <T> T executeGet(
            Connection connection,
            SimpleSqlBuilder builder,
            Entity entity,
            List<EntityField> entityFields,
            boolean output) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = BuilderKit.prepareStatement(connection, builder.sql.toString(), builder.values,output);
            rs = ps.executeQuery();
            if (rs.next()) {
                return EntitySqlUtils.buildRecord(rs, entity, entityFields);
            }
            return null;
        } finally {
            DaoUtils.close(rs);
            DaoUtils.close(ps);
        }
    }

    static <T> List<T> executeQuery(
            Connection connection,
            SimpleSqlBuilder builder,
            List<EntityField> entityFields,
            Entity entity,
            boolean output) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = BuilderKit.prepareStatement(
                    connection,
                    builder.sql.toString(),
                    builder.values,
                    output);
            rs = ps.executeQuery();
            return buildList(rs, entity, entityFields);
        } finally {
            DaoUtils.close(rs);
            DaoUtils.close(ps);
        }
    }


    static Object executeGetValue(
            Connection connection,
            final SimpleSqlBuilder builder,
            boolean output) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = BuilderKit.prepareStatement(connection,
                    builder.sql.toString(),
                    builder.values,output);
            rs = ps.executeQuery();
            if (rs.next()) {
                Object r = rs.getObject(1);
                return r;
            }
            return null;
        } finally {
            DaoUtils.close(rs);
            DaoUtils.close(ps);
        }


    }
}
