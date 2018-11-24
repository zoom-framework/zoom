package org.zoomdev.zoom.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.utils.PatternUtils;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.meta.ColumnMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * {@link Entity} 抽象基类
 */
public abstract class AbstractEntity implements Entity {
    EntityField[] entityFields;
    EntityField[] primaryKeys;
    String table;

    AutoEntity autoEntity;

    private Map<String, String> file2column;

    private Map<String, String> namesMap;

    AbstractEntity(
            String table,
            EntityField[] entityFields,
            EntityField[] primaryKeys,
            AutoEntity autoEntity,
            Map<String, String> namesMap) {
        this.table = table;
        this.entityFields = entityFields;
        this.primaryKeys = primaryKeys;
        this.autoEntity = autoEntity;
        this.file2column = new ConcurrentHashMap<String, String>();
        this.namesMap = namesMap;
    }


    @Override
    public EntityField[] getEntityFields() {
        return entityFields;
    }

    @Override
    public EntityField[] getPrimaryKeys() {
        return primaryKeys;
    }


    @Override
    public String getTable() {
        return table;
    }


    @Override
    public PreparedStatement prepareInsert(Connection connection, String sql) throws SQLException {
        // generate keys or next_val
        //"insert into xx values ()"
        if (autoEntity != null) {
            return autoEntity.prepareInsert(connection, sql);
        }
        return connection.prepareStatement(sql);
    }

    @Override
    public void afterInsert(Object data, PreparedStatement ps) throws SQLException {
        if (autoEntity != null) {
            autoEntity.afterInsert(data, ps);
        }
    }

    @Override
    public int getFieldCount() {
        return entityFields.length;
    }


    public EntityField getFieldByFieldName(String field) {
        for (EntityField entityField : entityFields) {
            if (entityField.getFieldName().equals(field)) {
                return entityField;
            }
        }
        return null;
    }

    /**
     * 尽一切可能
     * @return
     */
    protected EntityField tryToFind(String field){
        for (EntityField entityField : entityFields) {
            if (field.equalsIgnoreCase(entityField.getFieldName())) {
                return entityField;
            }
            if(field.equalsIgnoreCase(entityField.getColumnName())){
                return entityField;
            }
            ColumnMeta meta = entityField.getColumnMeta();
            if(meta!=null){
                if(field.equalsIgnoreCase(meta.getName())){
                    return entityField;
                }
            }

        }
        return null;
    }


    private String getColumnNameByFieldName(String field) {
        String column = file2column.get(field);
        if (column == null) {
            EntityField entityField = getFieldByFieldName(field);
            if (entityField != null) {
                column = entityField.getColumnName();
                file2column.put(field, column);
            }
            if (column == null && namesMap != null) {
                column = namesMap.get(field);
                if (column != null) {
                    file2column.put(field, column);
                }
            }
        }
        if (column == null) {
            throw new DaoException(
                    String.format("找不到字段%s对应的列名称,所有可能的字段列表为" +
                                    StringUtils.join(getAvailableFields(), ","),
                            field));
        }
        return column;
    }


    @Override
    public String getColumnName(String field) {
        if (field == null) {
            throw new NullPointerException("字段名称为空");
        }

        return getColumnNameByFieldName(field);
    }


    @Override
    public Set<String> getAvailableFields() {
        Set<String> list = new LinkedHashSet<String>();

        for (EntityField entityField : entityFields) {
            list.add(entityField.getFieldName());
        }

        // 对于Record来说，是没有这个字段的
        if (namesMap != null) {
            list.addAll(namesMap.keySet());

        }


        return list;
    }


    private static final Pattern AND_OR_PATTERN = Pattern.compile("[\\s]+(and)[\\s]+|[\\s]+(or)[\\s]+", Pattern.CASE_INSENSITIVE);

    @Override
    public String parseOn(String on) {
        if (StringUtils.isEmpty(on)) {
            throw new DaoException("请提供join的条件on");
        }
        final StringBuilder sb = new StringBuilder();
        final Set<String> joinAllFields = getAvailableFields();
        PatternUtils.visit(on, AND_OR_PATTERN, new PatternUtils.PatternVisitor() {
            @Override
            public void onGetPattern(Matcher matcher) {
                sb.append(matcher.group());
            }

            @Override
            public void onGetRest(String rest) {
                parseOnForOne(sb, rest, joinAllFields);
            }
        });
        //再来做一次替换
        return sb.toString();
    }


    private void parseOnForOne(final StringBuilder sb, String part, final Set<String> joinAllFields) {

        PatternUtils.visit(part, EntitySqlUtils.TABLE_AND_COLUMN_PATTERN, new PatternUtils.PatternVisitor() {
            @Override
            public void onGetPattern(Matcher matcher) {
                String str = matcher.group();
                if (str.contains(".")) {
                    //table + column
                    str = str.replace(" ", "");
                    if (!joinAllFields.contains(str)) {
                        throw new DaoException("找不到" + str + "对应的字段，当前所有可用字段为:"
                                + StringUtils.join(joinAllFields, ","));
                    }
                    sb.append(str);
                } else {
                    String column = getColumnNameByFieldName(str);
                    sb.append(column);
                }
            }

            @Override
            public void onGetRest(String rest) {
                sb.append(rest);
            }
        });

    }


    @Override
    public EntityField getFieldByColumnName(String columnName) {
        assert(columnName!=null);
        for(EntityField field : entityFields){
            if(columnName.equalsIgnoreCase(field.getColumnName())){
                return field;
            }
        }
        return null;
    }
}
