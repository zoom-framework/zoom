package com.jzoom.zoom.dao;

import com.jzoom.zoom.dao.adapters.StatementAdapterFactory;
import com.jzoom.zoom.dao.alias.NameAdapterFactory;
import com.jzoom.zoom.dao.driver.DbStructFactory;
import com.jzoom.zoom.dao.driver.SqlDriver;

import javax.sql.DataSource;

public interface Dao extends StatementAdapterFactory {
	
	/**
	 * 创建一个request范围的ActiveRecord
	 * @return
	 */
	Ar ar();
	
	/**
	 * 获取当前的activerecord，如果不存在返回null
	 * @return
	 */
	Ar getAr();
	
	Ar table(String table);

	Ar tables(String[] tables);


	/**
	 * 返回Entity active record
	 * @param type
	 * @param <T>
	 * @return
	 */
	<T> EAr<T> ar(Class<T> type);


    EAr<Record> record(String table);

    /**
	 * 获取数据库结构
	 * @return
	 */
	DbStructFactory getDbStructFactory() ;
	
	NameAdapterFactory getNameAdapterFactory();
	
	/**
	 * 清除缓存
	 */
	void clearCache();

    SqlDriver getDriver();

	DataSource getDataSource();
}
