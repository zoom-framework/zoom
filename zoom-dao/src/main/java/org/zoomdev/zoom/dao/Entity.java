package org.zoomdev.zoom.dao;

import org.apache.commons.lang3.ObjectUtils;
import org.zoomdev.zoom.dao.adapters.EntityField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
public interface Entity {

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
     * 绑定到哪个实体类
     *
     * @return
     */
    Class<?> getType();


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
     * 对数据进行校验,如果出错，那么直接跑出ValidateException异常
     * @param data
     */
    void validate(Object data);
}
