package com.github.ulwx.aka.dbutils.spring.multids;

import javax.sql.DataSource;

public class DSPoolType {
    public final static String TOMCAT_DB_POOL = "tomcatdbpool";
    public final static String ALiBABA_DRUID="druid";
    public final static String HikariCP="hikari";
    public final static String DBCP2="dbcp2";
    public final static String ShardingJDBC="ShardingJDBC";
    public final static String unknown="unknown";

    public static String decidePoolType(DataSource dataSource){
        String className=dataSource.getClass().getName();
        if(className.endsWith("DruidDataSource")){
            return ALiBABA_DRUID;
        }else if(className.endsWith("HikariDataSource")){
            return HikariCP;
        }else if(className.endsWith("jdbc.pool.DataSource")){
            return TOMCAT_DB_POOL;
        }else if(className.endsWith("dbcp2.BasicDataSource")){
            return DBCP2;
        }else if(className.contains("ShardingSphereDataSource")){
            return ShardingJDBC;
        }
        return unknown;

    }

}
