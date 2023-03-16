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
	private final Map<String, DataSource> targetDataSources=new ConcurrentHashMap<>();

	private volatile String defaultTargetDataSourceName;

	public String getDefaultTargetDataSourceName() {
		return defaultTargetDataSourceName;
	}

	public void setTargetDataSources(Map<String, DataSource> targetDataSources) {

		this.targetDataSources.putAll(targetDataSources);
	}

	@Nullable
	public Map<String, DataSource> getTargetDataSources() {
		return targetDataSources;
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
		DataSource dataSource = this.targetDataSources.get(lookupKey);
		return dataSource;
	}


	public void setDefaultTargetDataSourceName(String defaultTargetDataSourceName) {
		this.defaultTargetDataSourceName = defaultTargetDataSourceName;
	}

	@Nullable
	public abstract String determineCurrentLookupKey();

}
