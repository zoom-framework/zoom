package org.zoomdev.zoom.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.dao.BeanTableAdapter;
import org.zoomdev.zoom.dao.BeanTableInfo;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.annotations.Join;
import org.zoomdev.zoom.dao.annotations.Link;
import org.zoomdev.zoom.dao.annotations.Table;
import org.zoomdev.zoom.dao.meta.JoinMeta;

import java.util.Map;

class ZoomBeanTableAdapter implements BeanTableAdapter {


    protected class ZoomBeanTableInfo implements BeanTableInfo{
        public ZoomBeanTableInfo(String name) {
            this(new String[]{name},null);
        }

        public ZoomBeanTableInfo(String[] names, JoinMeta[] joinMetas) {
            this.names = names;
            this.joinMetas = joinMetas;
        }

        private String[] names;

        private JoinMeta[] joinMetas;


        @Override
        public String[] getTableNames() {
            return names;
        }

        @Override
        public JoinMeta[] getJoins() {
            return joinMetas;
        }
    }


    @Override
    public BeanTableInfo getTableInfo(Class<?> type) {
        Table table = type.getAnnotation(Table.class);
        if (table == null) {
            throw new DaoException("找不到"+type+"的Table标注，不能使用本方法绑定实体");
        }
        String tableName = table.value();
        if(StringUtils.isEmpty(tableName)){
            throw new DaoException("在标注中必须指定表名称"+table);
        }
        Link link = type.getAnnotation(Link.class);
        Join[] joins = null;
        if (link != null) {
            joins = link.value();
            JoinMeta[] joinMetas = getJoinConfigs(joins);
            //表
            String[] tables = new String[joins.length + 1];
            tables[0] = tableName;
            int index = 1;
            for (Join join : joins) {
                tables[index++] = join.table();
            }
            return new ZoomBeanTableInfo(tables,joinMetas);
        }
        return new ZoomBeanTableInfo(tableName);
    }

    private JoinMeta[] getJoinConfigs(Join[] joins) {
        if (joins == null) return null;
        JoinMeta[] joinMetas = new JoinMeta[joins.length];
        for (int i = 0; i < joins.length; ++i) {
            Join join = joins[i];
            //
            JoinMeta joinMeta = new JoinMeta();
            joinMeta.setOn(join.on());
            joinMeta.setTable(join.table());
            joinMeta.setType(join.type());

            joinMetas[i] = joinMeta;
        }
        return joinMetas;
    }
}
