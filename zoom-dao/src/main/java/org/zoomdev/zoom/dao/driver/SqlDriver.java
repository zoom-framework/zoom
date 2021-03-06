package org.zoomdev.zoom.dao.driver;


import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.adapters.StatementAdapterFactory;
import org.zoomdev.zoom.dao.impl.SimpleSqlBuilder;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.impl.TableBuildInfo;

import java.util.List;
import java.util.Map;

public interface SqlDriver extends StatementAdapterFactory {

    /**
     * 保护字段，如mysql加上 `` oracle有可能需要加上""
     *
     * @param name
     * @return
     */
    StringBuilder protectColumn(StringBuilder sb, String name);


    String protectColumn(String name);


    StringBuilder protectTable(StringBuilder sb, String name);

    /**
     * 获取数据适配器
     *
     * @param dataClass
     * @param columnClass
     * @return
     */
    StatementAdapter get(Class<?> dataClass, Class<?> columnClass);


    StringBuilder buildLimit(StringBuilder sql, List<Object> values, int position, int size);


    String formatColumnType(ColumnMeta column);

    void insertOrUpdate(StringBuilder sb, List<Object> values, String tableName, Map<String, Object> data, String... unikeys);


    String buildDropIfExists(String table);

    void buildTable(TableBuildInfo buildInfo, List<String> sqlList);

    String protectTable(String tableName);

    /**
     * 解析连接信息，获取表目录（数据库名称）
     *
     * @param url
     * @return
     */
    String getTableCatFromUrl(String url);

    <T> void buildInsertOrUpdate(SimpleSqlBuilder builder,
                                 Entity entity,
                                 T data,
                                 Filter<EntityField> filter,
                                 boolean ignoreNull,
                                 String[] unikeys);
}
