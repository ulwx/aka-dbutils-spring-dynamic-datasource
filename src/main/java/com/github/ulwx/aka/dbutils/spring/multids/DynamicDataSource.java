package com.github.ulwx.aka.dbutils.database.multids;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DynamicDataSource extends AbstractRoutingDataSource {


    public DynamicDataSource() {
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContext.get();
    }

    protected DataSource determineTargetDataSource(){
        return super.determineTargetDataSource();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new AkaProxyConnection(this);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }
}

