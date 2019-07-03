package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.dao.adapters.EntityField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * 绑定数据库字段和实体类字段
 * <p>
 * Column    ===========      Field
 * 解决：
 * 1、类型转化
 * 2、存储某一个类中的所有绑定关系
 * <p>
 * <p>
 * 在运行期间动态绑定或者在启动的时候就绑定
 *
 * @author jzoom
 */
public interface Entity<T> {

    /**
     * 所有绑定字段
     *
     * @return
     */
    EntityField[] getEntityFields();

    /**
     * 主键
     *
     * @return
     */
    EntityField[] getPrimaryKeys();

    /**
     * 获取field
     *
     * @param name field名称
     * @return
     */
    EntityField getFieldByFieldName(String name);

    /**
     * 通过字段查找
     *
     * @param columnName
     * @return
     */
    EntityField getFieldByColumnName(String columnName);

    /**
     * 绑定到哪个实体类
     *
     * @return
     */
    Class<T> getType();


    /**
     * 绑定到哪张表
     *
     * @return
     */
    String getTable();


    /**
     * 创建一个新的对象
     *
     * @return
     */
    Object newInstance();

    /**
     * 字段数量
     *
     * @return
     */
    int getFieldCount();


    /**
     * insert的时候
     *
     * @param connection
     * @return
     */
    PreparedStatement prepareInsert(Connection connection, String sql) throws SQLException;

    /**
     * 再插入之后的操作，如果自动生成的键，则在这个时候进行设置
     *
     * @param data
     * @param ps
     * @throws SQLException
     */
    void afterInsert(Object data, PreparedStatement ps) throws SQLException;

    /**
     * 从实体类字段名称获取数据库字段名称
     *
     * @param field
     * @return
     */
    String getColumnName(String field);

    /**
     * 为activerecord设置数据， table / join
     *
     * @param builder
     */
    void setQuerySource(SqlBuilder builder);

    /**
     * 解析join
     *
     * @param on
     * @return
     */
    String parseOn(String on);

    Set<String> getAvailableFields();


    List<EntityField> select(List<EntityField> holder, Iterable<String> fields);
}
