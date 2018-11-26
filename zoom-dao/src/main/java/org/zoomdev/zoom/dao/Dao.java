package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.dao.adapters.StatementAdapterFactory;
import org.zoomdev.zoom.dao.alias.AliasPolicyFactory;
import org.zoomdev.zoom.dao.alias.NameAdapter;
import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.zoomdev.zoom.dao.migrations.DatabaseBuilder;

import javax.sql.DataSource;

public interface Dao extends StatementAdapterFactory {

    /**
     * 创建一个request范围的ActiveRecord
     *
     * @return
     */
    Ar ar();


    Ar table(String table);


    /**
     * 返回Entity active record
     *
     * @param type
     * @param <T>
     * @return
     */
    <T> EAr<T> ar(Class<T> type);


    /**
     * @param tables
     * @return
     */
    EAr<Record> ar(String... tables);

    /**
     * 获取数据库结构
     *
     * @return
     */
    DbStructFactory getDbStructFactory();


    Entity getEntity(Class<?> type);

    Entity getEntity(String... tables);


    /**
     * 清除缓存
     */
    void clearCache();

    SqlDriver getDriver();

    DataSource getDataSource();


    AliasPolicyFactory getAliasPolicyMaker();


    DatabaseBuilder builder();


    <T> T execute(ConnectionExecutor connectionExecutor);


    /**
     * 获取到数据库连接字符串
     * @return
     */
    String getURL();


    void release();


    /**
     * 这是改名策略 {@link NameAdapter}
     * @param aDefault
     */
    void setNameAdapter(NameAdapter aDefault);


    /**
     * 是否输出sql
     * @param output
     */
    void setOutput(boolean output);



    /**
     * 数据库产品名称
     * @return
     */
    String getProductName();
}
