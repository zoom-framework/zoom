package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.alias.AliasPolicyMaker;
import org.zoomdev.zoom.dao.alias.NameAdapterFactory;
import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.driver.SqlDriver;

import javax.sql.DataSource;

public class HttpDao implements Dao {

  @Override
  public RawAr ar() {
    return null;
  }

  @Override
  public RawAr getAr() {
    return null;
  }

  @Override
  public Ar table(String table) {
    return null;
  }

  @Override
  public Ar tables(String[] tables) {
    return null;
  }

  @Override
  public <T> EAr<T> ar(Class<T> type) {
    return null;
  }

  @Override
  public EAr<Record> ar(String table) {
    return null;
  }

  @Override
  public EAr<Record> ar(String[] tables) {
    return null;
  }

  @Override
  public DbStructFactory getDbStructFactory() {
    return null;
  }

  @Override
  public NameAdapterFactory getNameAdapterFactory() {
    return null;
  }

  @Override
  public void clearCache() {}

  @Override
  public SqlDriver getDriver() {
    return null;
  }

  @Override
  public DataSource getDataSource() {
    return null;
  }

  @Override
  public AliasPolicyMaker getAliasPolicyMaker() {
    return null;
  }

  @Override
  public StatementAdapter getStatementAdapter(Class<?> fieldType, Class<?> columnType) {
    return null;
  }

  @Override
  public StatementAdapter getStatementAdapter(Class<?> columnType) {
    return null;
  }
}
