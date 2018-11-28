package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.dao.meta.JoinMeta;

/**
 * 标注的bean
 */
public interface BeanTableInfo {

    String[] getTableNames();

    JoinMeta[] getJoins();

}
