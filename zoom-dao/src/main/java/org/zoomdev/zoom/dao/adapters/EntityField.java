package org.zoomdev.zoom.dao.adapters;

import com.sun.istack.internal.Nullable;
import org.zoomdev.zoom.common.validate.Validator;
import org.zoomdev.zoom.dao.auto.AutoField;
import org.zoomdev.zoom.dao.meta.ColumnMeta;

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
     * 本来的字段名称,在多表情况下，可能与 {@link EntityField#getFieldName()} 不一致
     * @return
     */
    String getOriginalFieldName();


    /**
     * 字段类型,对于Record模式来说，除了Clob/Blob都与数据库一致
     * @return
     */
    Type getFieldType();

    /**
     * 对数据进行校验
     * @param data
     */
    void validate(Object data);

    /**
     * 数据库字段描述,并不是所有EntityField都有这个字段的，只有真实对应数据库中某个字段才有。
     * @return
     */
    ColumnMeta getColumnMeta();


    /**
     * 入库数据校验，对于表单提交，需要先使用本校验器校验之后，在使用其他校验器来校验。
     * @return
     */
    Validator getValidator();
}
