package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.RawAr;
import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.Collection;

public class NetworkDbStructFactory implements DbStructFactory {

  NetworkAdapter networkAdapter;

  /**
   * 这些都有对应的http接口
   *
   * @param ar
   * @return
   */
  @Override
  public Collection<String> getTableNames(RawAr ar) {
    return null;
  }

  @Override
  public TableMeta getTableMeta(RawAr ar, String tableName) {
    return null;
  }

  @Override
  public void fill(RawAr ar, TableMeta meta) {}

  @Override
  public Collection<TableNameAndComment> getNameAndComments(RawAr ar) {
    return null;
  }

  @Override
  public void clearCache() {}
}
