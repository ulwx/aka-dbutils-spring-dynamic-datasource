package com.github.ulwx.aka.dbutils.database.multids;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public class ConnectionResource {
    private Connection connection;
    private String  dataSourceName;
    private Boolean savepointsSupported;

    private int savepointCounter = 0;

    public Boolean getSavepointsSupported() {
        return savepointsSupported;
    }

    public void setSavepointsSupported(Boolean savepointsSupported) {
        this.savepointsSupported = savepointsSupported;
    }

    public int getSavepointCounter() {
        return savepointCounter;
    }

    public void setSavepointCounter(int savepointCounter) {
        this.savepointCounter = savepointCounter;
    }

    public ConnectionResource(String dataSourceName,  Connection connection) {
        this.connection = connection;
        this.dataSourceName = dataSourceName;
    }


    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Savepoint createSavepoint(String prefix) throws SQLException {
       this.savepointCounter++;
       return getConnection().setSavepoint(prefix + this.savepointCounter);
   }
}
