package org.zoomdev.zoom.dao.driver;


import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.adapters.StatementAdapterFactory;

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


    StringBuilder buildPage(StringBuilder sql, int position, int size);


    int position2page(int position, int size);

    int page2position(int page, int size);

    void insertOrUpdate(StringBuilder sb, List<Object> values, String tableName, Map<String, Object> data, String... unikeys);
}
