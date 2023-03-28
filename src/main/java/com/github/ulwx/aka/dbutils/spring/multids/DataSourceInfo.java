package com.github.ulwx.aka.dbutils.spring.multids;



import javax.sql.DataSource;

public class DataSourceInfo {

    /**
     * 最原始的数据源
     */
    private DataSource originalDataSource;
    /**
     *  originalDataSource可能被代理后的数据源，如果没有被代理则和originalDataSource相同
     */
    private DataSource dataSource;
    private String dsName="";
    private String poolType="";
    private String seata="";

    public String getPoolType() {
        return poolType;
    }


    public String getSeata() {
        return seata;
    }

    public void setSeata(String seata) {
        this.seata = seata;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public DataSourceInfo(String dsName, DataSource dataSource) {
        this.dsName=dsName;
        this.dataSource = dataSource;
        this.poolType=DSPoolType.decidePoolType(dataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public DataSource getOriginalDataSource() {
        return originalDataSource;
    }

    public void setOriginalDataSource(DataSource originalDataSource) {
        this.originalDataSource = originalDataSource;
    }

    @Override
    public String toString() {
        return "DataSourceInfo{" +
                "originalDataSource=" + originalDataSource.getClass() +
                ", dataSource=" + dataSource.getClass() +
                ", dsName='" + dsName + '\'' +
                ", poolType='" + poolType + '\'' +
                ", seata='" + seata + '\'' +
                '}';
    }
}
