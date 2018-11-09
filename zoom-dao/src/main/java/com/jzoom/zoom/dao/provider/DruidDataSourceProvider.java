package com.jzoom.zoom.dao.provider;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.dao.ConnectionDescriptor;
import com.jzoom.zoom.dao.DataSourceProvider;


public class DruidDataSourceProvider implements DataSourceProvider {
	// 基本属性 url、user、password
	// 初始连接池大小、最小空闲连接数、最大活跃连接数
	private int initialSize = 1;
	private int minIdle = 10;
	private int maxActive = 150;

	// 配置获取连接等待超时的时间(10秒)
	private long maxWait = 10 * 1000;
	
	private String username;
	private String password;
	
	private String url;

	// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
	private long timeBetweenEvictionRunsMillis = DruidDataSource.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
	// 配置连接在池中最小生存的时间
	private long minEvictableIdleTimeMillis = DruidDataSource.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
	// 配置发生错误时多久重连
	private long timeBetweenConnectErrorMillis = DruidDataSource.DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS;

	/**
	 * hsqldb - "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS" Oracle - "select 1
	 * from dual" DB2 - "select 1 from sysibm.sysdummy1" mysql - "select 1"
	 */
	private String validationQuery = "select 1 FROM DUAL";
	private boolean testWhileIdle = true;
	private boolean testOnBorrow = false;
	private boolean testOnReturn = false;
	
	private String driverClassName;

	// 是否打开连接泄露自动检测
	private boolean removeAbandoned = false;
	// 连接长时间没有使用，被认为发生泄露时长,5分钟(1分钟)
	private long removeAbandonedTimeoutMillis = 60 * 1000;
	// 发生泄露时是否需要输出 log，建议在开启连接泄露检测时开启，方便排错
	private boolean logAbandoned = true;

	// 是否缓存preparedStatement，即PSCache，对支持游标的数据库性能提升巨大，如 oracle、mysql 5.5 及以上版本
	// private boolean poolPreparedStatements = false; // oracle、mysql 5.5
	// 及以上版本建议为 true;

	// 只要maxPoolPreparedStatementPerConnectionSize>0,poolPreparedStatements就会被自动设定为true，使用oracle时可以设定此值。
	private int maxPoolPreparedStatementPerConnectionSize = -1;

	// 配置监控统计拦截的filters
	private String filters; // 监控统计："stat" 防SQL注入："wall" 组合使用： "stat,wall"
	
	
	public DruidDataSourceProvider() {
		
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	
	public DruidDataSourceProvider(ConnectionDescriptor descriptor) {
		this.url = descriptor.getJdbcUrl();
		this.username = descriptor.getUser();
		this.password = descriptor.getPassword();
		this.driverClassName = descriptor.getDriverClass();
	}
	
	public static class CloseableDataSource implements Cloneable, Destroyable,DataSource{
		final DruidDataSource ds;
		
		public CloseableDataSource(DruidDataSource ds) {
			assert(ds!=null);
			this.ds = ds;
		}
		@Override
		public void destroy() {
			this.ds.close();
		}
		@Override
		public PrintWriter getLogWriter() throws SQLException {
			return ds.getLogWriter();
		}
		@Override
		public void setLogWriter(PrintWriter out) throws SQLException {
			ds.setLogWriter(out);
		}
		@Override
		public void setLoginTimeout(int seconds) throws SQLException {
			ds.setLoginTimeout(seconds);
		}
		@Override
		public int getLoginTimeout() throws SQLException {
			return ds.getLoginTimeout();
		}

		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return ds.getParentLogger();
		}
		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return ds.unwrap(iface);
		}
		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return ds.isWrapperFor(iface);
		}
		@Override
		public Connection getConnection() throws SQLException {
			return ds.getConnection();
		}
		@Override
		public Connection getConnection(String username, String password) throws SQLException {
			
			return ds.getConnection(username, password);
		}
		
	}

	@SuppressWarnings("resource")
	public DataSource getDataSource() {
		DruidDataSource ds = new DruidDataSource();
		ds.setPoolPreparedStatements(true);
		ds.setUrl(url);
		ds.setUsername( username );
		ds.setPassword( password );
		ds.setDriverClassName(driverClassName);
		ds.setInitialSize(initialSize);
		ds.setMinIdle(minIdle);
		ds.setMaxActive(maxActive);
		ds.setMaxWait(maxWait);
		ds.setTimeBetweenConnectErrorMillis(timeBetweenConnectErrorMillis);
		ds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		ds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

		ds.setValidationQuery(validationQuery);
		ds.setTestWhileIdle(testWhileIdle);
		ds.setTestOnBorrow(testOnBorrow);
		ds.setTestOnReturn(testOnReturn);

		ds.setRemoveAbandoned(removeAbandoned);
		ds.setRemoveAbandonedTimeoutMillis(removeAbandonedTimeoutMillis);
		ds.setLogAbandoned(logAbandoned);

		// 只要maxPoolPreparedStatementPerConnectionSize>0,poolPreparedStatements就会被自动设定为true，参照druid的源码
		ds.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);

		
		if (!StringUtils.isEmpty(filters)) {
			try {
				ds.setFilters(filters);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		return ds;
	}


}
