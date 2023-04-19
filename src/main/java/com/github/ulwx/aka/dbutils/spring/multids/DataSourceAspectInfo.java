package com.github.ulwx.aka.dbutils.spring.multids;

public class DataSourceAspectInfo {
    private String dsName;
    private String dynamicDataSourceBeanName;
    private String laodBalancer;

    public DataSourceAspectInfo(String dsName, String dynamicDataSourceBeanName, String laodBalancer) {
        this.dsName = dsName;
        this.dynamicDataSourceBeanName = dynamicDataSourceBeanName;
        this.laodBalancer = laodBalancer;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public String getDynamicDataSourceBeanName() {
        return dynamicDataSourceBeanName;
    }

    public void setDynamicDataSourceBeanName(String dynamicDataSourceBeanName) {
        this.dynamicDataSourceBeanName = dynamicDataSourceBeanName;
    }

    public String getLaodBalancer() {
        return laodBalancer;
    }

    public void setLaodBalancer(String laodBalancer) {
        this.laodBalancer = laodBalancer;
    }
}
