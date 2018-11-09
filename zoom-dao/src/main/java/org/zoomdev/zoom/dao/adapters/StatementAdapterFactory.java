package org.zoomdev.zoom.dao.adapters;

import org.zoomdev.zoom.caster.Caster;

public interface StatementAdapterFactory {
    /**
     * 获取一个Statement的适配器
     * @param fieldType
     * @param columnType
     * @return
     */
    StatementAdapter getStatementAdapter(Class<?> fieldType, Class<?> columnType);

    /**
     * 获取一个Statement的适配器 , 在不知道需要适配什么类型的情况下
     * 只有columnType == Clob /Blob的时候有点特殊,需要使用Caster转成String或者byte[]再来入库
     * Blob接收InputStream / Clob 接收Reader , 在操作完成之后关闭
     * {@link Caster}
     *
     * @param columnType
     * @return
     */
    StatementAdapter getStatementAdapter(Class<?> columnType);


}
