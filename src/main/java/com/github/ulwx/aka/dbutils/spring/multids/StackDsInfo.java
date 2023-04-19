package com.github.ulwx.aka.dbutils.spring.multids;

public class StackDsInfo {
    private String dsName;
    private DataSourceInfo dataSourceInfo;
    private boolean shardingjdbcAndSeataAtContext=false;
    private boolean shardingjdbcAndSeataAtContextStart=false;
    public DataSourceInfo getDataSourceInfo() {
        return dataSourceInfo;
    }

    public void setDataSourceInfo(DataSourceInfo dataSourceInfo) {
        this.dataSourceInfo = dataSourceInfo;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public boolean isShardingjdbcAndSeataAtContext() {
        return shardingjdbcAndSeataAtContext;
    }

    public void setShardingjdbcAndSeataAtContext(boolean shardingjdbcAndSeataAtContext) {
        this.shardingjdbcAndSeataAtContext = shardingjdbcAndSeataAtContext;
    }

    public boolean isShardingjdbcAndSeataAtContextStart() {
        return shardingjdbcAndSeataAtContextStart;
    }

    public void setShardingjdbcAndSeataAtContextStart(boolean shardingjdbcAndSeataAtContextStart) {
        this.shardingjdbcAndSeataAtContextStart = shardingjdbcAndSeataAtContextStart;
    }
}
