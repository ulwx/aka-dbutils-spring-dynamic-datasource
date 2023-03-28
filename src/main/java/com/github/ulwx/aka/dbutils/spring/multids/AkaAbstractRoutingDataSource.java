package com.github.ulwx.aka.dbutils.spring.multids;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AkaAbstractRoutingDataSource extends AbstractDataSource implements InitializingBean {

	@Nullable
	private volatile ConcurrentHashMap<String, DataSourceInfo> targetDataSourceInfos=new ConcurrentHashMap<>();

	private volatile String defaultTargetDataSourceName;

	protected final static ThreadLocal<DataSourceInfo> Context =
			new ThreadLocal<DataSourceInfo>();

	public String getDefaultTargetDataSourceName() {
		return defaultTargetDataSourceName;
	}

	public void setTargetDataSourceInfos(ConcurrentHashMap<String, DataSourceInfo> targetDataSourceInfos) {
		this.targetDataSourceInfos=targetDataSourceInfos;
	}
	public void setTargetDataSources(Map<String, DataSource> targetDataSources) {
		for(String key: targetDataSources.keySet()){
			DataSource dataSource=targetDataSources.get(key);
			DataSourceInfo dataSourceInfo=new DataSourceInfo(key,dataSource);
			dataSourceInfo.setOriginalDataSource(dataSource);
			this.targetDataSourceInfos.put(key,dataSourceInfo);
		}
	}
	@Nullable
	public ConcurrentHashMap<String, DataSourceInfo> getTargetDataSources() {
		return targetDataSourceInfos;
	}



	@Override
	public Connection getConnection() throws SQLException {
		return determineTargetDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return determineTargetDataSource().getConnection(username, password);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (iface.isInstance(this)) {
			return (T) this;
		}
		return determineTargetDataSource().unwrap(iface);
	}


	public DataSource determineTargetDataSource() {

		String lookupKey = determineCurrentLookupKey();
		DataSourceInfo dataSourceInfo = this.targetDataSourceInfos.get(lookupKey);

		if(dataSourceInfo==null){
			throw new RuntimeException("无法通过"+lookupKey+"找到相应的数据源！");
		}
		Context.set(dataSourceInfo);
		DataSource dataSource= dataSourceInfo.getDataSource();
		return dataSource;
	}


	public void setDefaultTargetDataSourceName(String defaultTargetDataSourceName) {
		this.defaultTargetDataSourceName = defaultTargetDataSourceName;
	}

	@Nullable
	public abstract String determineCurrentLookupKey();

}
