package org.zoomdev.zoom.dao.adapters;

import com.sun.istack.internal.Nullable;
import org.zoomdev.zoom.caster.Caster;

public interface StatementAdapterFactory {
    /**
     * 获取一个Statement的适配器
     * <p>
     * <p>
     * fieldType = null
     * 获取一个Statement的适配器 , 在不知道需要适配什么类型的情况下
     * 只有columnType == Clob /Blob的时候有点特殊,需要使用Caster转成String或者byte[]再来入库
     * Blob接收InputStream / Clob 接收Reader , 在操作完成之后关闭
     * {@link Caster}
     * <p>
     * columnType=null
     * 不知道数据库里面是什么类型?当成一般类型来处理
     *
     * @param fieldType
     * @param columnType
     * @return
     */
    StatementAdapter getStatementAdapter(
            @Nullable
                    Class<?> fieldType,

            @Nullable
                    Class<?> columnType);


}
