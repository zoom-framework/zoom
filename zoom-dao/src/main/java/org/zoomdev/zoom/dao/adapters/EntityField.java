package org.zoomdev.zoom.dao.adapters;

import com.sun.istack.internal.Nullable;
import org.zoomdev.zoom.dao.AutoField;

import java.lang.reflect.Type;

public interface EntityField extends StatementAdapter {

    /**
     * 从对应的实例中取出字段对应的值
     *
     * @param target
     * @return
     */
    Object get(Object target);

    /**
     * 设置值
     *
     * @param data
     * @param fieldValue
     */
    void set(Object data, Object fieldValue);




    /**
     * 将数据库中的值转为实体类的值
     *
     * @param columnValue
     * @return
     */
    Object getFieldValue(Object columnValue);

    /**
     * 获取数据库字段名，原始字段名称
     *
     * @return
     */
    String getColumnName();

    /**
     * 获取实体类的字段名称
     *
     * @return
     */
    String getFieldName();

    /**
     * 获取在select中应该填充的值,经过适配的名称
     *
     * @return
     */
    String getSelectColumnName();



    /**
     * PrepareStatement的适配器
     *
     * @return
     */
    StatementAdapter getStatementAdapter();


    /**
     * 自动生成字段（不一定有）
     *
     * @return
     */
    @Nullable
    AutoField getAutoField();


    /**
     * 字段类型,对于Record模式来说，除了Clob/Blob都与数据库一致
     * @return
     */
    Type getFieldType();
}
